package com.nirmata.workflow;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.nirmata.workflow.details.TaskExecutorSpec;
import com.nirmata.workflow.details.WorkflowManagerImpl;
import com.nirmata.workflow.executor.TaskExecutor;
import com.nirmata.workflow.models.TaskType;
import com.nirmata.workflow.queue.QueueFactory;
import com.nirmata.workflow.queue.zookeeper.ZooKeeperQueueFactory;
import org.apache.curator.framework.CuratorFramework;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

public class WorkflowManagerBuilder
{
    private QueueFactory queueFactory = new ZooKeeperQueueFactory();
    private String instanceName;
    private CuratorFramework curator;
    private final List<TaskExecutorSpec> specs = Lists.newArrayList();

    public static WorkflowManagerBuilder builder()
    {
        return new WorkflowManagerBuilder();
    }

    public WorkflowManagerBuilder withCurator(CuratorFramework curator)
    {
        this.curator = Preconditions.checkNotNull(curator, "curator cannot be null");
        return this;
    }

    public WorkflowManagerBuilder withQueueFactory(QueueFactory queueFactory)
    {
        this.queueFactory = Preconditions.checkNotNull(queueFactory, "queueFactory cannot be null");
        return this;
    }

    public WorkflowManagerBuilder withInstanceName(String instanceName)
    {
        this.instanceName = Preconditions.checkNotNull(instanceName, "instanceName cannot be null");
        return this;
    }

    public WorkflowManagerBuilder addingTaskExecutor(TaskExecutor taskExecutor, int qty, Collection<TaskType> taskTypes)
    {
        specs.add(new TaskExecutorSpec(taskExecutor, qty, taskTypes));
        return this;
    }

    public WorkflowManager build()
    {
        return new WorkflowManagerImpl(curator, queueFactory, instanceName, specs);
    }

    private WorkflowManagerBuilder()
    {
        try
        {
            instanceName = InetAddress.getLocalHost().getHostName();
        }
        catch ( UnknownHostException e )
        {
            instanceName = "unknown";
        }
    }
}
