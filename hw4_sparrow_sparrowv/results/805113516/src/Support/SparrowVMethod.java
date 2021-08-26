package Support;

import java.util.ArrayList;
import java.util.HashMap;

public class SparrowVMethod {
    private HashMap<String, LivenessInterval> variableLiveIntervals;
    private HashMap<String, LivenessInterval> SCC;

    public SparrowVMethod() {
        variableLiveIntervals = new HashMap<>();
        SCC = new HashMap<>();
    }

    //////////////////////////////////////////////////////////////////
    //SCC ANALYSIS////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    public void newSCC(String labelName, int dfn) {

        LivenessInterval li = new LivenessInterval();
        li.def = dfn;
        li.use = -1;

        SCC.put(labelName, li);
    }

    public void endSCC(String labelName, int dfn) {
        LivenessInterval li = getSCC(labelName);

        if (li != null) {
            li.use = dfn;
            SCC.put(labelName, li);
        }
    }

    public LivenessInterval getSCC(String labelName) {
        return SCC.get(labelName);
    }

    public void cleanSCC() {
        ArrayList<String> toRemove = new ArrayList<>();

        for (String key: SCC.keySet()) {
            LivenessInterval li = SCC.get(key);
            if (li.use < 0) {
                toRemove.add(key);
            }
        }

        for (String key: toRemove) {
            SCC.remove(key);
        }
    }


    //////////////////////////////////////////////////////////////////
    //LIVENESS ANALYSIS///////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    public void assignDef(String varName, int dfn) {
        if (!variableLiveIntervals.containsKey(varName)) {
            LivenessInterval li = new LivenessInterval();
            li.def = dfn;
            li.use = -1;
            variableLiveIntervals.put(varName, li);
        }

    }

    public void assignUse(String varName, int dfn) {
        LivenessInterval li = getLivenessInterval(varName);
        li.use = dfn;
        variableLiveIntervals.put(varName, li);
    }

    public LivenessInterval getLivenessInterval(String varName) {
        return variableLiveIntervals.get(varName);
    }

    public void cleanLiveness() {
        for (String varName: variableLiveIntervals.keySet()) {
            LivenessInterval li = variableLiveIntervals.get(varName);
            if (li.use < 0) {
                li.use = li.def;
                variableLiveIntervals.put(varName, li);
            }
        }
    }

    public void extendLiveness() {
        for (String varName: variableLiveIntervals.keySet()) {
            LivenessInterval varRange = variableLiveIntervals.get(varName);
            for (String key: SCC.keySet()) {
                LivenessInterval SCCRange = SCC.get(key);

                //variable defined within
                if (varRange.def < SCCRange.use && SCCRange.def < varRange.def) {
                    varRange.def = SCCRange.def;
                }
            }
            variableLiveIntervals.put(varName, varRange);

            for (String key: SCC.keySet()) {
                LivenessInterval SCCRange = SCC.get(key);

                //variable declared before, used during
                if (varRange.def <= SCCRange.def && SCCRange.def < varRange.use && varRange.use < SCCRange.use) {
                    varRange.use = SCCRange.use;
                }
            }
            variableLiveIntervals.put(varName, varRange);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    //PRINT HELPERS//////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    public void printSCC() {
        for (String key: SCC.keySet()) {
            LivenessInterval SCCRange = SCC.get(key);
            System.out.println(key + " " + SCCRange.def + " " + SCCRange.use);
        }
    }

    public void printLiveness() {
        for (String varName: variableLiveIntervals.keySet()) {
            LivenessInterval varRange = variableLiveIntervals.get(varName);
            System.out.println(varName + " " + varRange.def + " " + varRange.use);
        }
    }

}