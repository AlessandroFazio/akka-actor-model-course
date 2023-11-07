package akka;

import akka.actor.testkit.typed.CapturedLogEvent;;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import org.example.akka.blockchain.ControllerBehavior;
import org.example.akka.blockchain.WorkerBehavior;
import org.example.akka.model.Block;
import org.example.akka.model.HashResult;
import org.example.akka.utils.BlocksData;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MiningTest {

    @Test
    void testMiningReturnsNullIfNonceNotInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor =
                BehaviorTestKit.create(WorkerBehavior.create());

        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ControllerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 0, 5, testInbox.getRef());
        testActor.run(message);
        List<CapturedLogEvent> logMessages = testActor.getAllLogEntries();

        assertEquals(1, logMessages.size());
        assertEquals("null", logMessages.get(0).message());
        assertEquals(Level.DEBUG, logMessages.get(0).level());
    }

    @Test
    public void testMiningReturnsHashResultIfNonceIsInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor =
                BehaviorTestKit.create(WorkerBehavior.create());

        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ControllerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 868490, 5, testInbox.getRef());
        testActor.run(message);
        List<CapturedLogEvent> logMessages = testActor.getAllLogEntries();

        String expectedHash = "0000072c7db1bd19c9336fd638fa7ea89d83d73b85e0cc9e53e648375b202f65";
        int expectedNonce = 868491;
        String expectedLogMessage = Integer.toString(expectedNonce) + " : " + expectedHash;

        assertEquals(1, logMessages.size());
        assertEquals(expectedLogMessage, logMessages.get(0).message());
        assertEquals(Level.DEBUG, logMessages.get(0).level());
    }

    @Test
    public void testHashResultMessageIsReceivedWhenNonceIsInRange() {
        BehaviorTestKit<WorkerBehavior.Command> testActor =
                BehaviorTestKit.create(WorkerBehavior.create());

        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ControllerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 868490, 5, testInbox.getRef());
        testActor.run(message);

        String expectedHash = "0000072c7db1bd19c9336fd638fa7ea89d83d73b85e0cc9e53e648375b202f65";
        int expectedNonce = 868491;

        HashResult expectedHashResult = new HashResult();
        expectedHashResult.foundAHash(expectedHash, expectedNonce);

        testInbox.expectMessage(new ControllerBehavior.HashResultCommand(expectedHashResult));
    }

    @Test
    public void testNullMessageIsReceivedWhenNonceNotInRange() {

        BehaviorTestKit<WorkerBehavior.Command> testActor =
                BehaviorTestKit.create(WorkerBehavior.create());

        Block block = BlocksData.getNextBlock(0, "0");

        TestInbox<ControllerBehavior.Command> testInbox = TestInbox.create();

        WorkerBehavior.Command message = new WorkerBehavior.Command(block, 0, 5, testInbox.getRef());
        testActor.run(message);

        assertFalse(testInbox.hasMessages());
    }
}
