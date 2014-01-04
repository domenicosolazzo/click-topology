package storm.cookbook;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import storm.cookbook.VisitStatsBolt;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(value = Parameterized.class)
public class TestVisitStatsBolt extends StormTestCase {

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { "true", "1" }, { "false", "0" }};
        return Arrays.asList(data);
    }

    private String unique;
    private int expected;

    public TestVisitStatsBolt(String unique, String expected){
        this.unique = unique;
        this.expected = Integer.parseInt(expected);
    }

    @Test
    public void testExecute(){
        VisitStatsBolt bolt = new VisitStatsBolt();

        final OutputCollector collector = context.mock(OutputCollector.class);
        bolt.prepare(null, null, collector);

        final Tuple tuple = getTuple();
        context.checking(new Expectations(){{
            atLeast(1).of(tuple).getStringByField(storm.cookbook.Fields.UNIQUE);will(returnValue(unique));
            oneOf(collector).emit(new Values(1,expected));
        }});

        bolt.execute(tuple);
        context.assertIsSatisfied();
    }
}