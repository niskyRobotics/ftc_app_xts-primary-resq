/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import dalvik.system.DexFile;

/**
 *
 * @author David Sargent - FTC5395, maths222, Qualcomm
 * @since 1.0
 */
public class FtcOpModeRegister implements OpModeRegister {
    private static String TAG = "FTC_OP_MODE_REGISTER::";

    /**
     * The Op Mode Manager will call this method when it wants stopMode list of all
     * available op modes. Add your op mode to the list to enable it.
     *
     * @param mgr op mode manager
     */
    public void register(final OpModeManager mgr) {
        // manager.register(NetworkOpMode.class.getSimpleName(), new NetworkOpMode());
        try {
            FallbackOpModeRegister userFallback = new FallbackOpModeRegister();
            userFallback.register(mgr);

            final LinkedList<String> noCheckList = new LinkedList<>();
            noCheckList.add("com.google");
            noCheckList.add("io.netty");

            // Get the possible classes
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            Context context = (Application) method.invoke(null, (Object[]) null);
            DexFile df = new DexFile(context.getPackageCodePath());

            // Put the correct classes in stopMode HashMap out of the possible classes
            HashMap<String, LinkedList<Class>> opModes = new HashMap<>();

            // Migrate the enum to stopMode LinkedList
            LinkedList<String> classes = new LinkedList<>(Collections.list(df.entries()));
            for (String klazz : classes) {
                boolean shouldBreak = false;
                for (String noContinue : noCheckList) {
                    if (klazz.contains(noContinue)) {
                        shouldBreak = true;
                        break;
                    }
                }
                if (shouldBreak) {
                    continue;
                }

                Class teleOpClass = TeleOp.class;
                Class autoClass = Autonomous.class;
                Class disabledClass = Disabled.class;

                Class currentClass;
                try {
                    currentClass = Class.forName(klazz, false, context.getClassLoader());
                } catch (NoClassDefFoundError ex) {
                    Log.w(TAG, klazz + " " + ex.toString(), ex);
                    if (klazz.contains("$")) {
                        klazz = klazz.substring(0, klazz.indexOf("$") - 1);
                    }
                    noCheckList.add(klazz);
                    break;
                } catch (ClassNotFoundException ex) {
                    Log.w(TAG, klazz + " " + ex.toString(), ex);
                    if (klazz.contains("$")) {
                        klazz = klazz.substring(0, klazz.indexOf("$") - 1);
                    }
                    noCheckList.add(klazz);
                    break;
                }
                if (!currentClass.isAnnotationPresent(disabledClass)) {
                    if (currentClass.isAnnotationPresent(teleOpClass)) {
                        Annotation annotation = currentClass.getAnnotation(teleOpClass);
                        String name = ((TeleOp) annotation).pairWithAuto();
                        if (name.equals("")) {
                            int i = 0;
                            while (opModes.containsKey(Integer.toString(i))) {
                                i++;
                            }
                            LinkedList<Class> temp = new LinkedList<>();
                            temp.add(currentClass);
                            opModes.put(Integer.toString(i), temp);
                        } else {
                            if (opModes.containsKey(name)) {
                                opModes.get(name).add(currentClass);
                            } else {
                                LinkedList<Class> temp = new LinkedList<>();
                                temp.add(currentClass);
                                opModes.put(name, temp);
                            }
                        }
                    }
                    if (currentClass.isAnnotationPresent(autoClass)) {
                        Annotation annotation = currentClass.getAnnotation(autoClass);
                        String name = ((Autonomous) annotation).pairWithTeleOp();
                        if (name.equals("")) {
                            int i = 0;
                            while (opModes.containsKey(Integer.toString(i))) {
                                i++;
                            }
                            LinkedList<Class> temp = new LinkedList<>();
                            temp.add(currentClass);
                            opModes.put(Integer.toString(i), temp);
                        } else {
                            if (opModes.containsKey(name)) {
                                opModes.get(name).add(currentClass);
                            } else {
                                LinkedList<Class> temp = new LinkedList<>();
                                temp.add(currentClass);
                                opModes.put(name, temp);
                            }
                        }
                    }
                }
            }

            // Sort the linked lists within the Hash Map
            Comparator<Class> firstComparator =
                    new Comparator<Class>() {
                        @Override
                        public int compare(Class lhs, Class rhs) {
                            if (lhs.isAnnotationPresent(TeleOp.class) &&
                                    rhs.isAnnotationPresent(TeleOp.class)) {
                                String lName = ((TeleOp) lhs.getAnnotation(TeleOp.class)).name();
                                String rName = ((TeleOp) lhs.getAnnotation(TeleOp.class)).name();

                                if (lName.equals("")) {
                                    lName = lhs.getSimpleName();
                                }

                                if (rName.equals("")) {
                                    rName = rhs.getSimpleName();
                                }
                                return lName.compareTo(rName);
                            }

                            if (lhs.isAnnotationPresent(Autonomous.class) &&
                                    rhs.isAnnotationPresent(TeleOp.class)) {
                                return 1;
                            } else if (rhs.isAnnotationPresent(Autonomous.class) &&
                                    lhs.isAnnotationPresent(TeleOp.class)) {
                                return -1;
                            }

                            if (lhs.isAnnotationPresent(Autonomous.class) &&
                                    rhs.isAnnotationPresent(Autonomous.class)) {
                                String lName = ((Autonomous) lhs.getAnnotation(Autonomous.class)).name();
                                String rName = ((Autonomous) rhs.getAnnotation(Autonomous.class)).name();

                                if (lName.equals("")) {
                                    lName = lhs.getSimpleName();
                                }

                                if (rName.equals("")) {
                                    rName = rhs.getSimpleName();
                                }
                                return lName.compareTo(rName);
                            }

                            return -1;
                        }
                    };
            for (String key : opModes.keySet()) {
                Collections.sort(opModes.get(key), firstComparator);
            }

            // Sort the map by keys, after discarding the old keys, use the new key from
            // the first item in each LinkedList, and change from stopMode HashMap to stopMode TreeMap
            TreeMap<String, LinkedList<Class>> sortedOpModes = new TreeMap<>();
            for (String key : opModes.keySet()) {
                Class<? extends OpMode> opMode = opModes.get(key).getFirst();
                String name;
                if (opMode.isAnnotationPresent(TeleOp.class)) {
                    name = opMode.getAnnotation(TeleOp.class).name();
                } else if (opMode.isAnnotationPresent(Autonomous.class)) {
                    name = opMode.getAnnotation(Autonomous.class).name();
                } else {
                    name = opMode.getSimpleName();
                }

                if (name.equals("")) {
                    name = opMode.getSimpleName();
                }
                sortedOpModes.put(name, opModes.get(key));
            }

            for (LinkedList<Class> opModeList : sortedOpModes.values()) {
                for (Class opMode : opModeList) {
                    mgr.register(getOpModeName(opMode), opMode);
                }
            }

        } catch (final ClassNotFoundException e) {
            Log.wtf(TAG, e.toString(), e);
        } catch (final NoSuchMethodException e) {
            Log.wtf(TAG, e.toString(), e);
        } catch (final IllegalArgumentException e) {
            Log.e(TAG, e.toString(), e);
        } catch (final IllegalAccessException e) {
            Log.e(TAG, e.toString(), e);
        } catch (final InvocationTargetException e) {
            Log.wtf(TAG, e.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }

        /*try {
            final LinkedList<String> noCheckList = new LinkedList<>();
            noCheckList.add("com.google");
            noCheckList.add("io.netty");
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            Context context = (Application) method.invoke(null, (Object[]) null);
            DexFile df = new DexFile(context.getPackageCodePath());
            Thread process = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Class<?> activityThreadClass;
                    try {
                        activityThreadClass = Class.forName("android.app.ActivityThread");

                        final Method method = activityThreadClass.getMethod("currentApplication");
                        Context context = (Application) method.invoke(null, (Object[]) null);
                        DexFile df = new DexFile(context.getPackageCodePath());

                        LinkedList<String> classes = new LinkedList<>(Collections.list(df.entries()));
                        for (String klazz : classes) {
                            boolean shouldBreak = false;
                            for (String noContinue : noCheckList) {
                                if (klazz.contains(noContinue)) {
                                    shouldBreak = true;
                                    break;
                                }
                            }
                            if (shouldBreak) {
                                continue;
                            }

                            Class activeClass = ActiveOpMode.class;
                            Class currentClass;
                            try {
                                currentClass = Class.forName(klazz, false, context.getClassLoader());
                            } catch (NoClassDefFoundError ex) {
                                Log.w(TAG, klazz + " " + ex.toString(), ex);
                                return;
                            } catch (ClassNotFoundException ex) {
                                Log.w(TAG, klazz + " " + ex.toString(), ex);
                                return;
                            }
                            Log.i("CLASSES", currentClass.toString());
                            if(currentClass.isAnnotationPresent(activeClass)) {
                                Annotation annotation = currentClass.getAnnotation(activeClass);
                                String name = ((ActiveOpMode) annotation).value();
                                if(name.length() == 0) {
                                    name = currentClass.getSimpleName();
                                }
                                manager.register(name, currentClass);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            process.start();
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (final NoSuchMethodException e) {
            // handle exception
        } catch (final IllegalArgumentException e) {
            // handle exception
        } catch (final IllegalAccessException e) {
            // handle exception
        } catch (final InvocationTargetException e) {
            // handle exception
        } catch (IOException e) {
            // handle exception
        }*/
    }


    public String getOpModeName(Class<OpMode> opMode) {
        String name;
        if (opMode.isAnnotationPresent(TeleOp.class)) {
            name = opMode.getAnnotation(TeleOp.class).name();
        } else if (opMode.isAnnotationPresent(Autonomous.class)) {
            name = opMode.getAnnotation(Autonomous.class).name();
        } else {
            name = opMode.getSimpleName();
        }

        if (name.equals("")) {
            name = opMode.getSimpleName();
        }
        return name;
    }

    /*
     * Uncomment any of the following lines if you want to register an op mode.
     */

    //manager.register("AdafruitRGBExample", AdafruitRGBExample.class);
    //manager.register("MRRGBExample", MRRGBExample.class);
    //manager.register("ColorSensorDriver", ColorSensorDriver.class);
    //manager.register("HTRGBExample", HTRGBExample.class);

    //manager.register("IrSeekerOp", IrSeekerOp.class);
    //manager.register("CompassCalibration", CompassCalibration.class);
    //manager.register("I2cAddressChangeExample", LinearI2cAddressChange.class);


    //manager.register("NxtTeleOp", NxtTeleOp.class);
    
    //manager.register("LinearK9TeleOp", LinearK9TeleOp.class);
    //manager.register("LinearIrExample", LinearIrExample.class);

    
    //manager.register ("PushBotManual1", PushBotManual1.class);
    //manager.register ("PushBotAutoSensors", PushBotAutoSensors.class);
    //manager.register ("PushBotIrEvent", PushBotIrEvent.class);
    
    //manager.register ("PushBotManualSensors", PushBotManualSensors.class);
    //manager.register ("PushBotOdsDetectEvent", PushBotOdsDetectEvent.class);
    //manager.register ("PushBotOdsFollowEvent", PushBotOdsFollowEvent.class);
    //manager.register ("PushBotTouchEvent", PushBotTouchEvent.class);    
    
    //manager.register("PushBotDriveTouch", PushBotDriveTouch.class);
    //manager.register("PushBotIrSeek", PushBotIrSeek.class);
    //manager.register("PushBotSquare", PushBotSquare.class);

    

}
