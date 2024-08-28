/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.transport.service.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for creating thread factory
 */
public class ProcessorThreadFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorThreadFactory.class);
    private static final String MSG = "aia-messaging-";
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger();
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger();
    final String name;
    final int stack;
    final int threadPriority;

    /**
     * Constructor with Local Name and stack local size.
     * @param nameLocal
     *            Local name.
     * @param stackLocal
     *            stack size that needs to be allocate,
     */
    public ProcessorThreadFactory(final String nameLocal, final int stackLocal) {
        this(nameLocal, stackLocal, Thread.NORM_PRIORITY);
    }

    /**
     * Constructor with Local Name and stack local size.
     * @param nameLocal
     *            Local name.
     * @param stackLocal
     *            stack size that needs to be allocate,
     * @param threadPriority
     *            thread priority.
     */
    public ProcessorThreadFactory(final String nameLocal, final int stackLocal, final int threadPriority) {
        final SecurityManager securityMgr = System.getSecurityManager();
        group = (securityMgr != null) ? securityMgr.getThreadGroup() : Thread.currentThread().getThreadGroup();
        name = MSG + nameLocal + POOL_NUMBER.getAndIncrement();
        stack = stackLocal;
        this.threadPriority = threadPriority;
        if (LOG.isInfoEnabled()) {
            LOG.info("Process Thread factory initlized with [nameLocal=" + nameLocal + ", stackLocal=" + stackLocal + ",Thread PRIORITY="
                    + threadPriority + "]");
        }
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thisThread = new Thread(group, runnable, name + threadNumber.getAndIncrement(), stack);
        if (thisThread.isDaemon()) {
            thisThread.setDaemon(false);
        }
        thisThread.setPriority(threadPriority);
        return thisThread;
    }

    /**
     * @return thread group.
     */
    public ThreadGroup getGroup() {
        return group;
    }

}
