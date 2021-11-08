
package de.toem.impulse.extension.examples.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;

import de.toem.impulse.cells.record.Scope;
import de.toem.impulse.cells.record.Signal;
import de.toem.impulse.domain.TimeBase;
import de.toem.impulse.samples.IBinarySamplesWriter;
import de.toem.impulse.samples.IEventSamplesWriter;
import de.toem.impulse.samples.IFloatSamplesWriter;
import de.toem.impulse.samples.IIntegerSamplesWriter;
import de.toem.impulse.samples.ILogicSamplesWriter;
import de.toem.impulse.samples.ISample;
import de.toem.impulse.samples.ISamples;
import de.toem.impulse.samples.IStructSamplesWriter;
import de.toem.impulse.samples.ISamples.ProcessType;
import de.toem.impulse.samples.ISamples.SignalDescriptor;
import de.toem.impulse.samples.ISamples.SignalType;
import de.toem.impulse.serializer.AbstractSingleDomainRecordReader;
import de.toem.impulse.serializer.ParseException;
import de.toem.impulse.values.Logic;
import de.toem.impulse.values.StructMember;
import de.toem.toolkits.pattern.element.ICover;
import de.toem.toolkits.pattern.properties.IPropertyModel;
import de.toem.toolkits.pattern.threading.IProgress;

public class SimpleReader extends AbstractSingleDomainRecordReader {

    private long current;
    private int changed;

    private Signal int1;
    private Signal int2;
    private Signal int3;

    private Signal float1;
    private Signal float2;
    private Signal float3;
    private Signal float4;
    private Signal float5;

    private Signal binary1;

    private Signal event1;
    private Signal event2;
    private Signal event3;
    private Signal event4;

    private Signal logic1;
    private Signal logic2;
    private Signal logic3;
    private Signal logic4;

    private Signal struct1;
    private Signal struct2;
    private Signal struct3;

    // ========================================================================================================================
    // Construct
    // ========================================================================================================================

    public SimpleReader() {
        super();
    }

    public SimpleReader(String id, InputStream in) {
        super(id, in);
    }

    // ========================================================================================================================
    // Properties
    // ========================================================================================================================

    static public IPropertyModel getPropertyModel() {
        return getDefaultPropertyModel().add("myParameter", "auto", "My Parameter", null, "A text parameter").add("myParameter", 0, "My Parameter",
                null, "Another integer parameter");
    }

    static public IPropertyModel getPropertyModel(Class sz) {
        return getPropertyModel();
    }

    // ========================================================================================================================
    // Applicable
    // ========================================================================================================================

    @Override
    protected int isApplicable(String name, String contentType) {
        if (name != null && !name.endsWith(".example"))
            return NOT_APPLICABLE;
        // if (contentType != null && !contentType.equals("my_content_type"))
        // return NOT_APPLICABLE;
        return 10 | SNIFF_PREFERRED; // try to get 10 bytes to check - isApplicable(byte[] buffer) will be called
    }

    @Override
    protected int isApplicable(byte[] buffer) {
        if (false) // check buffer
            return APPLICABLE;
        return NOT_APPLICABLE;
    }

    // ========================================================================================================================
    // Stream Reader
    // ========================================================================================================================

    @Override
    public boolean supportsStreaming() {
        return true;
    }

    @Override
    synchronized public ICover flush() {
        if (changed != CHANGED_NONE) {
            changed = CHANGED_NONE;
            return super.doFlush(current);
        }
        return null;
    }

    @Override
    public int hasChanged() {
        return changed;
    }

    // ========================================================================================================================
    // Parser
    // ========================================================================================================================

    @Override
    protected void parse(IProgress progress, InputStream in) throws ParseException {

        // text reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        int linesProcessed = 0;

        // binary reader
        // byte[] buffer = new byte[4096];
        // int filled = 0;
        // int read = 0;

        try {

            // ========================================================================================================================
            // Create record
            // ========================================================================================================================

            initRecord("Example File", TimeBase.ms);

            // ========================================================================================================================
            // Add signals - you may add signals later while parsing
            // ========================================================================================================================

            // Scope scope = addScope(null, "Integer Signals");
            // int1 = addSignal(scope, "Integer1", "An integer", ProcessType.Discrete, SignalType.Integer, SignalDescriptor.DEFAULT);

            addExampleSignals(); // uncomment after you created your signals

            changed = CHANGED_RECORD;

            // ========================================================================================================================
            // Wait to start and open
            // ========================================================================================================================

            // wait for start
            waitStreaming(progress);

            // open
            synchronized (this) {
                current = 0;
                open(current);
                changed = changed > CHANGED_CURRENT ? changed : CHANGED_CURRENT;
            }

            // ========================================================================================================================
            // Parse and write samples
            // ========================================================================================================================

            // text line loop
            while ((line = reader.readLine()) != null && (progress == null || !progress.isCanceled())) {
                parse(line);
                linesProcessed++;
            }

            // binary data loop
            // while((read = in.read(buffer, filled, buffer.length-filled))>=0 && && (progress == null || !progress.isCanceled())){
            // filled+=read;
            // int used = parse(buffer,filled);
            // System.arraycopy(buffer, used, buffer, 0, filled-used);
            // filled -= used;
            // }

            // here some sample data
            writeExampleSamples(); // uncomment after you created your signals

            // ========================================================================================================================
            // Close
            // ========================================================================================================================

            synchronized (this) {
                close(current + 1);
                changed = CHANGED_NONE;
            }

        } catch (ParseException e) {
            throw e;
        } catch (IOException e) {

        } catch (Throwable e) {
            throw new ParseException(linesProcessed, e.getMessage(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }

    }

    // ========================================================================================================================
    // Line/Block Parser
    // ========================================================================================================================

    // synchronized private int parse(byte[] data, int len) throws ParseException {

    synchronized private void parse(String line) throws ParseException {
        if (line.trim().isEmpty())
            return;

        // ========================================================================================================================
        // Parse lines and write samples
        // ========================================================================================================================

        // current = currentTime; // TimeBase.ms
        // IIntegerSamplesWriter writer = (IIntegerSamplesWriter) getWriter(int1);
        // writer.write(current, false, value);

        changed = changed > CHANGED_SIGNALS ? changed : CHANGED_SIGNALS;
    }

    // ========================================================================================================================
    // Example Data
    // ========================================================================================================================

    protected void addExampleSignals() {

        // Integer signals
        Scope scope = addScope(null, "Integer Signals");
        int1 = addSignal(scope, "Integer1", "An integer", ProcessType.Discrete, SignalType.Integer, SignalDescriptor.DEFAULT);
        int2 = addSignal(scope, "Integer2", "A big integer", ProcessType.Discrete, SignalType.Integer, SignalDescriptor.DEFAULT);
        int3 = addSignal(scope, "Integer3", "XY", ProcessType.Discrete, SignalType.IntegerArray,
                new SignalDescriptor(ISample.CONTENT_DEFAULT, 2, ISample.INTEGER_ACCURACY_DEFAULT, ISample.FORMAT_DEFAULT));
        // Float signals
        scope = addScope(null, "Float Signals");
        float1 = addSignal(scope, "Signal1", "My first float Signal (limitet to 32 bits)", ProcessType.Discrete, SignalType.Float,
                SignalDescriptor.DEFAULT);
        float2 = addSignal(scope, "Signal2", "My second float Signal", ProcessType.Discrete, SignalType.Float, SignalDescriptor.DEFAULT);
        float3 = addSignal(scope, "Signal3", "Another float (limitet to 32 bits)", ProcessType.Discrete, SignalType.Float, SignalDescriptor.DEFAULT);
        float4 = addSignal(scope, "Signal4", "Rectangle", ProcessType.Discrete, SignalType.Float, SignalDescriptor.DEFAULT);
        float5 = addSignal(scope, "Signal5", "XY", ProcessType.Discrete, SignalType.FloatArray,
                new SignalDescriptor(ISample.CONTENT_DEFAULT, 2, ISample.FLOAT_ACCURACY_64, ISample.FORMAT_DEFAULT));
        // Binary signals
        scope = addScope(null, "Binary Signals");
        binary1 = addSignal(scope, "Image", "An image signal", ProcessType.Discrete, SignalType.Binary,
                new SignalDescriptor(ISample.CONTENT_IMAGE, -1));
        scope = addScope(null, "Event Signals");
        event1 = addSignal(scope, "Event1", "Simple events", ProcessType.Discrete, SignalType.Event, SignalDescriptor.DEFAULT);
        event2 = addSignal(scope, "Event2", "Enum events with ints", ProcessType.Discrete, SignalType.Event, SignalDescriptor.DEFAULT);
        event3 = addSignal(scope, "Event3", "Enum events with strings", ProcessType.Discrete, SignalType.Event, SignalDescriptor.DEFAULT);
        event4 = addSignal(scope, "Event4", "2 Enums", ProcessType.Discrete, SignalType.EventArray,
                new SignalDescriptor(ISample.CONTENT_GANTT, 2, ISample.ACCURACY_DEFAULT, ISample.FORMAT_DEFAULT));
        scope = addScope(null, "Logic Signals");
        logic1 = addSignal(scope, "Logic1", "A logic signal", ProcessType.Discrete, SignalType.Logic, SignalDescriptor.LogicWidth(1));
        logic2 = addSignal(scope, "Logic2", "An 8bit  logic signal", ProcessType.Discrete, SignalType.Logic, SignalDescriptor.LogicWidth(8));
        logic3 = addSignal(scope, "Logic3", "An 2048 bit logic signal", ProcessType.Discrete, SignalType.Logic, SignalDescriptor.LogicWidth(2048));
        logic4 = addSignal(scope, "Logic4", "An 64 bit logic signal", ProcessType.Discrete, SignalType.Logic, SignalDescriptor.LogicWidth(64));
        scope = addScope(null, "Struct Signals");
        struct1 = addSignal(scope, "Struct1", "A log signal", ProcessType.Discrete, SignalType.Struct, SignalDescriptor.DEFAULT);
        struct2 = addSignal(scope, "Struct2", "A transaction signal", ProcessType.Discrete, SignalType.Struct,
                new SignalDescriptor(ISample.CONTENT_TRANSACTION, ISample.FORMAT_DEFAULT));
        struct3 = addSignal(scope, "Struct3", "A struct signal", ProcessType.Discrete, SignalType.Struct, SignalDescriptor.StructGantt);
    }

    protected void writeExampleSamples() {
        writeIntegerSamples();
        writeFloatSamples();
        writeBinarySamples();
        writeEventSamples();
        writeLogicSamples();
        writeStructSamples();
    }

    protected void writeIntegerSamples() {

        // add array member informations
        getWriter(int3).setMember(0, "X", 0, null, ISample.FORMAT_DEFAULT);
        getWriter(int3).setMember(1, "Y", 0, null, ISample.FORMAT_DEFAULT);

        // get the writers
        IIntegerSamplesWriter integerWriter = (IIntegerSamplesWriter) getWriter(int1);

        for (long t = 0; t < 100000; t += 2) {

            // write time as integer
            integerWriter.write(t, false, (int) t - 10000);
            integerWriter.write(t + 1, false, (long) t + 1 - 10000);

            ((IIntegerSamplesWriter) getWriter(int3)).write(t, false,
                    new long[] { (long) (Math.sin((t + 100) / 3000.0) * 10.0), (long) (Math.cos((t + 100) / 3000.0) * 10.0) });

        }

        integerWriter = (IIntegerSamplesWriter) getWriter(int2);
        integerWriter.write(10000, false, BigInteger.valueOf(0));
        integerWriter.write(11000, false, BigInteger.valueOf(1));
        integerWriter.write(12000, false, BigInteger.valueOf(-1));
        integerWriter.write(13000, false, BigInteger.valueOf(-255));
        integerWriter.write(14000, false, BigInteger.valueOf(255));
        integerWriter.write(15000, false, BigInteger.valueOf(256));
        integerWriter.write(16000, false, BigInteger.valueOf(-256));
        integerWriter.write(20000, false, BigInteger.valueOf(-5));
        BigInteger integer = new BigInteger("C00000012345678C00000012345678C00000012345678C00000012345678", 16);
        integerWriter.write(30000, false, integer);
        integer = new BigInteger("-123456789012345678901234567890", 10);
        integerWriter.write(40000, false, integer);
        integer = new BigInteger("123456789012345678901234567890", 10);
        integerWriter.write(50000, false, integer);
    }

    protected void writeFloatSamples() {

        // add array member informations
        getWriter(float5).setMember(0, "X", 0, null, ISample.FORMAT_DEFAULT);
        getWriter(float5).setMember(1, "Y", 0, null, ISample.FORMAT_DEFAULT);

        // And continue until 1000 ns
        for (long t = 0; t < 100000; t += 10) {

            // write the sin and cos data
            ((IFloatSamplesWriter) getWriter(float1)).write(t, false, Math.sin((t + 100) / 3000.0) * 10.0);
            ((IFloatSamplesWriter) getWriter(float2)).write(t, false, Math.cos((t + 200) / 1000.0) * 10.0 + Math.cos((t + 400) / 100.0) * 3.0
                    + Math.signum(Math.cos((t + 400) / 50.0)) * 1.0 + Math.cos((t + 100) / 10.0) * 2.0 + Math.random() * 2.5);
            ((IFloatSamplesWriter) getWriter(float3)).write(t, false, Math.sin((t + 300) / 6000.0) * 10.0 + 10.00001);
            ((IFloatSamplesWriter) getWriter(float4)).write(t, false,
                    Math.signum(Math.cos((t + 400) / 500.0)) * 1.0 > 0 ? new BigDecimal("13.234563456734562323454534355345345345345234234234234")
                            : new BigDecimal("0.030034004340344000340043430043434000343430023423400234234"));
            double f = Math.sin(t / 30000.0) * 1;
            ((IFloatSamplesWriter) getWriter(float5)).write(t, false,
                    new double[] { Math.sin((t + 100) / 3000.0) * 10.0, Math.cos((t + 100) / 3000.0) * 10.0 * f });
        }
    }

    protected void writeBinarySamples() {

        // images
        IBinarySamplesWriter imageWriter = (IBinarySamplesWriter) getWriter(binary1);

        // try {
        // ImageData[] imageData = new ImageLoader().load(Bundles.getBundleEntryAsStream(ImpulseToolkitExtension.BUNDLE_ID, "input2.gif"));
        // if (imageData != null)
        // for (ImageData data : imageData) {
        // ImageLoader loader = new ImageLoader();
        // loader.data = new ImageData[] { data };
        // ByteArrayOutputStream out = new ByteArrayOutputStream();
        // loader.save(out, TLK.IMAGE_PNG);
        // out.close();
        // imageWriter.write(t, false, out.toByteArray());
        // t += 20;
        // }

        // } catch (IOException e) {
        // }
    }

    protected void writeEventSamples() {

        // simple events
        IEventSamplesWriter eventWriter = (IEventSamplesWriter) getWriter(event1);
        eventWriter.write(12000, false);
        eventWriter.write(22000, false);

        // enum events with strings
        eventWriter = (IEventSamplesWriter) getWriter(event2);
        eventWriter.write(13000, false, "Start");
        eventWriter.write(14000, false, "Continue");
        eventWriter.write(15000, false, "Stop");
        eventWriter.write(25000, false, "Destroy");
        eventWriter.write(30000, false, "Start");

        // enum events with ints
        eventWriter = (IEventSamplesWriter) getWriter(event3);
        for (int n = 0; n < 32; n++)
            eventWriter.write(0 + n * 2000, false, n);

        // enum events with arrays
        eventWriter = (IEventSamplesWriter) getWriter(event4);
        eventWriter.setMember(0, "State", "STATE", ISample.FORMAT_TEXT);
        eventWriter.setMember(1, "Event", "EVENT", ISample.FORMAT_TEXT);
        eventWriter.write(10000, false, new String[] { null, "X1" });
        eventWriter.write(30000, false, new String[] { "Starting", "X2" });
        eventWriter.write(50000, false, new String[] { "Started", "Off" });
        eventWriter.write(60000, false, new String[] { null, "X1" });
        eventWriter.write(70000, false, new String[] { "Running", "Of2" });
        eventWriter.write(80000, false, new String[] { "Stopping", "Of3" });
        eventWriter.write(90000, false, new String[] { "Setopped", "Of4" });
        eventWriter.write(95000, false, new String[] { "Down", "X1" });
    }


    final static byte STATE_0_BITS = (byte) ISample.STATE_0_BITS;
    final static byte STATE_1_BITS = (byte) ISample.STATE_1_BITS;
    final static byte STATE_Z_BITS = (byte) ISample.STATE_Z_BITS;
    final static byte STATE_X_BITS = (byte) ISample.STATE_X_BITS;
    final static byte STATE_L_BITS = (byte) ISample.STATE_L_BITS;
    final static byte STATE_H_BITS = (byte) ISample.STATE_H_BITS;
    final static byte STATE_U_BITS = (byte) ISample.STATE_U_BITS;
    final static byte STATE_W_BITS = (byte) ISample.STATE_W_BITS;
    final static byte STATE_D_BITS = (byte) ISample.STATE_D_BITS;

    protected void writeLogicSamples() {

        // get the writers
        ILogicSamplesWriter logicWriter = (ILogicSamplesWriter) getWriter(logic1);

        // And continue until 1000 ns
        for (long t = 0; t < 100000; t += 10) {

            // write a rectangle
            logicWriter.write(t, false, ISample.STATE_LEVEL_2, t % 20 == 0 ? STATE_0_BITS : STATE_1_BITS);
        }

        logicWriter = (ILogicSamplesWriter) getWriter(logic2);
        logicWriter.write(0, false, ISample.STATE_LEVEL_16, STATE_U_BITS);
        logicWriter.write(10000, false, ISample.STATE_LEVEL_2, STATE_0_BITS);
        logicWriter.write(20000, false, ISample.STATE_LEVEL_2, STATE_0_BITS, new byte[] { STATE_1_BITS, STATE_1_BITS, STATE_0_BITS, STATE_1_BITS }, 0, 4);
        logicWriter.write(30000, false, STATE_0_BITS, "11XX");

        logicWriter = (ILogicSamplesWriter) getWriter(logic3);
        logicWriter.write(20000, false, ISample.STATE_LEVEL_2, STATE_1_BITS);
        logicWriter.write(50000, false, ISample.STATE_LEVEL_2, STATE_0_BITS);
        logicWriter.write(70000, false, STATE_U_BITS, "111100001111000011110000");
        logicWriter.write(80000, false, STATE_U_BITS, "000011110000111100001111");

        logicWriter = (ILogicSamplesWriter) getWriter(logic4);
        logicWriter.write(0, false, Logic.valueOf("11XXZZUU00"));
        logicWriter.write(20000, false, Logic.valueOf("uuXXZZUU00"));
        logicWriter.write(40000, false, Logic.valueOf("xxuuXXZZUU00"));
        logicWriter.write(60000, false, Logic.valueOf(0xff00));
        logicWriter.write(80000, false, Logic.valueOf(0xffffaa23L));

    }

    protected void writeStructSamples() {
        // log
        StructMember members[] = new StructMember[2];
        members[0] = new StructMember("Message", StructMember.STRUCT_TYPE_TEXT, null, ISamples.FORMAT_DEFAULT);
        members[1] = new StructMember("Data", StructMember.STRUCT_TYPE_INTEGER, null, ISamples.FORMAT_HEXADECIMAL);

        IStructSamplesWriter logWriter = (IStructSamplesWriter) getWriter(struct1);

        members[0].setValue("Start");
        members[1].setValue(100);
        logWriter.write(100, false, members);

        members[0].setValue("Continue");
        members[1].setValue(500);
        logWriter.write(500, false, members);

        // transaction
        StructMember members0[] = new StructMember[2];
        members0[0] = new StructMember("Message", StructMember.STRUCT_TYPE_TEXT, null, ISamples.FORMAT_DEFAULT);
        members0[1] = new StructMember("Data", StructMember.STRUCT_TYPE_INTEGER, null, ISamples.FORMAT_HEXADECIMAL);
        StructMember members1[] = new StructMember[2];
        members1[0] = new StructMember("Message", StructMember.STRUCT_TYPE_TEXT, null, ISamples.FORMAT_DEFAULT);
        members1[1] = new StructMember("Address", StructMember.STRUCT_TYPE_INTEGER, null, ISamples.FORMAT_HEXADECIMAL);

        IStructSamplesWriter transWriter = (IStructSamplesWriter) getWriter(struct2);

        members0[0].setValue("Do 1");
        members0[1].setValue(0);
        transWriter.write(100, false, /* id */0, /* order */IStructSamplesWriter.GO_FIRST, /* layer */0, members0);

        members1[0].setValue("Do 2");
        members1[1].setValue(0);
        transWriter.write(150, false, 1, IStructSamplesWriter.GO_FIRST, 1, members1);

        members0[1].setValue(90);
        transWriter.write(300, false, 0, IStructSamplesWriter.GO_LAST, 0, members0);

        members1[1].setValue(90);
        transWriter.write(350, false, 1, IStructSamplesWriter.GO_LAST, 1, members1);

        members0[0].setValue("Do 3");
        members0[1].setValue(0);
        transWriter.write(350, false, 2, IStructSamplesWriter.GO_FIRST, 0, members0);
        members0[1].setValue(90);
        transWriter.write(600, false, 2, IStructSamplesWriter.GO_LAST, 0, members0);

    }
}
