package dk.brics.tajs.analysis.dom.html5;

import dk.brics.tajs.analysis.Exceptions;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.dom.DOMObjects;
import dk.brics.tajs.analysis.dom.DOMWindow;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;

import java.util.Arrays;
import java.util.Set;

import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMProperty;
import static dk.brics.tajs.util.Collections.newSet;

public class AudioParam {

    public static ObjectLabel CONSTRUCTOR;

    public static ObjectLabel PROTOTYPE;

    public static ObjectLabel INSTANCES;

    public static void build(State s) {

        // Scraped from google chrome:
        Set<String> scrapedFunctionPropertyNames = newSet(Arrays.asList("setValueAtTime", "linearRampToValueAtTime", "exponentialRampToValueAtTime", "setTargetAtTime", "setValueCurveAtTime", "cancelScheduledValues"));

        CONSTRUCTOR = new ObjectLabel(DOMObjects.AUDIOPARAM_CONSTRUCTOR, ObjectLabel.Kind.FUNCTION);
        PROTOTYPE = new ObjectLabel(DOMObjects.AUDIOPARAM_PROTOTYPE, ObjectLabel.Kind.OBJECT);
        INSTANCES = new ObjectLabel(DOMObjects.AUDIOPARAM_INSTANCES, ObjectLabel.Kind.OBJECT);

        // Constructor Object
        s.newObject(CONSTRUCTOR);
        s.writePropertyWithAttributes(CONSTRUCTOR, "length", Value.makeNum(0).setAttributes(true, true, true));
        s.writePropertyWithAttributes(CONSTRUCTOR, "prototype", Value.makeObject(PROTOTYPE).setAttributes(true, true, true));
        s.writeInternalPrototype(CONSTRUCTOR, Value.makeObject(InitialStateBuilder.FUNCTION_PROTOTYPE));
        s.writeProperty(DOMWindow.WINDOW, "AudioParam", Value.makeObject(CONSTRUCTOR));

        // Prototype Object
        s.newObject(PROTOTYPE);
        s.writeInternalPrototype(PROTOTYPE, Value.makeObject(InitialStateBuilder.OBJECT_PROTOTYPE));

        // Multiplied Object
        s.newObject(INSTANCES);
        s.writeInternalPrototype(INSTANCES, Value.makeObject(PROTOTYPE));

        /*
         * Properties.
         */
        createDOMProperty(s, INSTANCES, "value", Value.makeAnyNum());
        createDOMProperty(s, INSTANCES, "defaultValue", Value.makeAnyNumUInt().setReadOnly());

        s.multiplyObject(INSTANCES);
        // FIXME AudioParam is an interface, GainNode is an example of an (yet unmodelled) instance
        INSTANCES = INSTANCES.makeSingleton().makeSummary();

        /*
         * Functions.
         */

        ObjectLabel dummyFunction = new ObjectLabel(DOMObjects.AUDIOPARAM_TAJS_UNSUPPORTED_FUNCTION, ObjectLabel.Kind.FUNCTION);
        for (String propertyName : scrapedFunctionPropertyNames) {
            // explicit implementation of: createDOMFunction(s, PROTOTYPE, DOMObjects.WEBGLRENDERINGCONTEXT_SOME_FUNCTION, propertyName, ***Value.makeAnyNum()***);
            s.newObject(dummyFunction);
            s.writePropertyWithAttributes(PROTOTYPE, propertyName, Value.makeObject(dummyFunction).setAttributes(true, false, false));
            s.writePropertyWithAttributes(dummyFunction, "length", Value.makeAnyNum().setAttributes(true, true, true));
            s.writeInternalPrototype(dummyFunction, Value.makeObject(InitialStateBuilder.FUNCTION_PROTOTYPE));
        }
    }

    public static Value evaluate(DOMObjects nativeObject, FunctionCalls.CallInfo call, State s, Solver.SolverInterface c) {
        switch (nativeObject) {
            case AUDIOPARAM_CONSTRUCTOR:
                Exceptions.throwTypeError(s, c);
                s.setToNone();
            case AUDIOPARAM_TAJS_UNSUPPORTED_FUNCTION:
                throw new AnalysisException("This function from AudioParam is not yet supported: " + call.getJSSourceNode().getSourceLocation());
            default: {
                throw new UnsupportedOperationException("Unsupported Native Object: " + nativeObject);
            }
        }
    }
}
