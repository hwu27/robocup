package core.ai.behaviorTree.robotTrees.fielder;

import core.ai.GameInfo;
import core.ai.GameState;
import core.ai.behaviorTree.nodes.NodeState;
import core.ai.behaviorTree.nodes.compositeNodes.CompositeNode;
import core.ai.behaviorTree.nodes.conditionalNodes.ConditionalNode;
import core.ai.behaviorTree.robotTrees.fielder.defense.DefenseRootNode;
import core.ai.behaviorTree.robotTrees.fielder.offense.OffenseRootNode;
import core.ai.behaviorTree.robotTrees.fielder.specificStateFunctions.*;
import core.fieldObjects.robot.Ally;

// root node of fielder tree
// if game is in open play, plays offense or defense
// if referee command, takes appropriate action
// checks for change in game status at defined frequency
// if game status different, kills current branch execution
// and starts execution of correct branch

public class FielderRootNode extends CompositeNode {

    private final ConditionalNode haveBall;
    private final OffenseRootNode offense;
    private final DefenseRootNode defense;

    private final HaltNode haltNode;
    private final StopNode stopNode;
    private final DirectFreeNode prepareDirectFreeNode;
    private final IndirectFreeNode prepareIndirectFreeNode;
    private final KickoffNode prepareKickoffNode;
    private final PenaltyNode preparePenaltyNode;
    private final NormalStartNode normalStartNode;
    private final ForceStartNode forceStartNode;
    private final BallPlacementNode ballPlacementNode;

    private GameState stateCurrentlyRunning;
    private boolean onOffense;

    private Thread branchThread;

    public FielderRootNode(Ally ally) {
        super("Fielder Root");
        this.haveBall = new ConditionalNode() {
            @Override
            public boolean conditionSatisfied() {
                return GameInfo.getPossessBall();
            }
        };
        this.offense = new OffenseRootNode(ally);
        this.defense = new DefenseRootNode(ally);

        this.haltNode = new HaltNode(ally);
        this.stopNode = new StopNode(ally);
        this.prepareDirectFreeNode = new DirectFreeNode(ally);
        this.prepareIndirectFreeNode = new IndirectFreeNode(ally);
        this.prepareKickoffNode = new KickoffNode(ally);
        this.preparePenaltyNode = new PenaltyNode(ally);
        this.normalStartNode = new NormalStartNode(ally);
        this.forceStartNode = new ForceStartNode(ally);
        this.ballPlacementNode = new BallPlacementNode(ally);

        this.stateCurrentlyRunning = GameInfo.getCurrState();
        this.onOffense = false;

        this.branchThread = new Thread(); // this may be changed to ScheduledExecutorService
    }

    @Override
    public NodeState execute() {
        // TODO
        // at a desired frequency, check if game state and ball possession has changed
        // if so, switch branch
        if (this.stateCurrentlyRunning != GameInfo.getCurrState()) {
            switchBranch();
        }
        else if (stateCurrentlyRunning == GameState.OPEN_PLAY) {
            if (NodeState.isSuccess(this.haveBall.execute()) != this.onOffense) {
                switchBranch();
            }
        }
        return NodeState.RUNNING;
    }

    private void switchBranch() {
        // kill current thread
        // start new thread with executeCorrectBranch() as target
        executeCorrectBranch();
        this.stateCurrentlyRunning = GameInfo.getCurrState();
    }

    private void executeCorrectBranch() {
        switch (GameInfo.getCurrState()) {
            case OPEN_PLAY:
                if (onOffense) {
                    this.offense.execute();
                }
                else {
                    this.defense.execute();
                }
                break;
            case HALT:
                this.haltNode.execute();
                break;
            case STOP:
                this.stopNode.execute();
                break;
            case PREPARE_DIRECT_FREE:
                this.prepareDirectFreeNode.execute();
                break;
            case PREPARE_INDIRECT_FREE:
                this.prepareIndirectFreeNode.execute();
                break;
            case PREPARE_KICKOFF:
                this.prepareKickoffNode.execute();
                break;
            case PREPARE_PENALTY:
                this.preparePenaltyNode.execute();
                break;
            case NORMAL_START:
                this.normalStartNode.execute();
                break;
            case FORCE_START:
                this.forceStartNode.execute();
                break;
            case BALL_PLACEMENT:
                this.ballPlacementNode.execute();
                break;
        }
    }

}
