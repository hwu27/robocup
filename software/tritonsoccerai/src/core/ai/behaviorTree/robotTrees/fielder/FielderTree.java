package core.ai.behaviorTree.robotTrees.fielder;

import core.ai.behaviorTree.BehaviorTree;
import core.ai.behaviorTree.robotTrees.fielder.fielderRoot.FielderRootNode;
import proto.filtered_object.Robot;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Tree that defines the behavior for fielder allies
 * There will be a separate tree for each ally fielder running concurrently during competition
 */
public class FielderTree extends BehaviorTree {

    private final Robot fielder;

    public FielderTree(Robot fielder, ScheduledThreadPoolExecutor executor) {
        super();
        this.root = new FielderRootNode(fielder, executor);
        this.fielder = fielder;
    }

}
