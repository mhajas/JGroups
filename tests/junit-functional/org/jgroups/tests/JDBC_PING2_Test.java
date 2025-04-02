package org.jgroups.tests;

import org.jgroups.Global;
import org.jgroups.JChannel;
import org.jgroups.util.Util;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Misc tests for {@link org.jgroups.protocols.JDBC_PING2}
 * @author Bela Ban
 * @since  5.4, 5.3.7
 */
@Test(groups=Global.FUNCTIONAL,singleThreaded=true)
public class JDBC_PING2_Test {
    protected static final String CLUSTER="jdbc-test";

    public void testClusterFormedAfterRestart() throws Exception {
        try(var a=createChannel("jdbc-pg.xml", "A")) {
            a.connect(CLUSTER);
            for(int i=1; i <= 5000; i++) {
                long start=System.nanoTime();
                try(var b=createChannel("jdbc-pg.xml", "B")) {
                    b.connect(CLUSTER);
                    Util.waitUntilAllChannelsHaveSameView(10000, 500, a,b);
                    long time=System.nanoTime()-start;
                    System.out.printf("-- join #%d took %s\n", i, Util.printTime(time, TimeUnit.NANOSECONDS));
                }
            }
        }
    }

    protected static JChannel createChannel(String cfg, String name) throws Exception {
        return new JChannel(cfg).name(name);
    }
}
