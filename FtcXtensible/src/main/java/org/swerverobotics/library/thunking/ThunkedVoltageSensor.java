package org.swerverobotics.library.thunking;

import com.qualcomm.robotcore.hardware.*;

/**
 * A VoltageSensor that can be called on the main() thread.
 */
public class ThunkedVoltageSensor implements VoltageSensor
    {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    public VoltageSensor target;   // can only talk to him on the loop thread

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    private ThunkedVoltageSensor(VoltageSensor target)
        {
        if (target == null) throw new NullPointerException("null " + this.getClass().getSimpleName() + " target");
        this.target = target;
        }

    static public ThunkedVoltageSensor create(VoltageSensor target)
        {
        return target instanceof ThunkedVoltageSensor ? (ThunkedVoltageSensor)target : new ThunkedVoltageSensor(target);
        }

    //----------------------------------------------------------------------------------------------
    // VoltageSensor
    //----------------------------------------------------------------------------------------------

    @Override public double getVoltage()
        {
        return (new ResultableThunk<Double>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.getVoltage();
                }
            }).doReadOperation();
        }
        @Override
        public String getDeviceName() {
            return (new ResultableThunk<String>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getDeviceName();
                }
            }).doReadOperation();
        }

        @Override
        public String getConnectionInfo() {
            return (new ResultableThunk<String>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getConnectionInfo();
                }
            }).doReadOperation();
        }

        @Override
        public int getVersion() {
            return (new ResultableThunk<Integer>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getVersion();
                }
            }).doReadOperation();
        }

        @Override
        public void close() {
            new ResultableThunk<String>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.close();
                }
            }.doWriteOperation();
        }

    }
