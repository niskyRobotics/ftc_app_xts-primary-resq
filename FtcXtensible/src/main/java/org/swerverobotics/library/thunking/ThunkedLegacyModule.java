package org.swerverobotics.library.thunking;

import com.qualcomm.robotcore.hardware.*;

import java.util.concurrent.locks.Lock;

public class ThunkedLegacyModule implements LegacyModule
    {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    public LegacyModule target;   // can only talk to him on the loop thread

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    private ThunkedLegacyModule(LegacyModule target)
        {
        if (target == null) throw new NullPointerException("null " + this.getClass().getSimpleName() + " target");
        this.target = target;
        }

    static public ThunkedLegacyModule create(LegacyModule target)
        {
        return target instanceof ThunkedLegacyModule ? (ThunkedLegacyModule)target : new ThunkedLegacyModule(target);
        }

    //----------------------------------------------------------------------------------------------
    // LegacyModule interface
    //----------------------------------------------------------------------------------------------

    @Override public void enableNxtI2cReadMode(final int physicalPort, final int i2cAddress, final int memAddress, final int memLength)
        {
        (new NonwaitingThunk()
            {
            @Override protected void actionOnLoopThread()
                {
                target.enableNxtI2cReadMode(physicalPort, i2cAddress, memAddress, memLength);
                }
            }).doWriteOperation();
        }

    @Override public void enableNxtI2cWriteMode(final int physicalPort, final int i2cAddress, final int memAddress, final int initialValues)
        {
        (new NonwaitingThunk()
            {
            @Override protected void actionOnLoopThread()
                {
                target.enableNxtI2cWriteMode(physicalPort, i2cAddress, memAddress, initialValues);
                }
            }).doWriteOperation();
        }

    @Override public void enableAnalogReadMode(final int physicalPort)
        {
        (new NonwaitingThunk()
            {
            @Override protected void actionOnLoopThread()
                {
                target.enableAnalogReadMode(physicalPort);
                }
            }).doWriteOperation();
        }

    @Override public void enable9v(final int physicalPort, final boolean enable)
        {
        (new NonwaitingThunk()
            {
            @Override protected void actionOnLoopThread()
                {
                target.enable9v(physicalPort, enable);
                }
            }).doWriteOperation();
        }

    @Override public void setDigitalLine(final int physicalPort, final int line, final boolean set)
        {
        (new NonwaitingThunk()
            {
            @Override protected void actionOnLoopThread()
                {
                target.setDigitalLine(physicalPort, line, set);
                }
            }).doWriteOperation();
        }

//    @Override public byte[] readLegacyModuleCache(final int physicalPort)
//        {
//        return (new ResultableThunk<byte[]>()
//            {
//            @Override protected void actionOnLoopThread()
//                {
//                this.result = target.readLegacyModuleCache(physicalPort);
//                }
//            }).doReadOperation();
//        }
//
//    @Override public void writeLegacyModuleCache(final int physicalPort, final byte[] data)
//        {
//        (new NonwaitingThunk()
//            {
//            @Override protected void actionOnLoopThread()
//                {
//                target.writeLegacyModuleCache(physicalPort, data);
//                }
//            }).doWriteOperation();
//        }

    @Override public byte[] readAnalog(final int physicalPort)
        {
        return (new ResultableThunk<byte[]>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.readAnalog(physicalPort);
                }
            }).doReadOperation();
        }

        @Override
        public Lock getI2cReadCacheLock(final int i) {
            return (new ResultableThunk<Lock>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getI2cReadCacheLock(i);
                }
            }).doReadOperation();
        }

        @Override
        public Lock getI2cWriteCacheLock(final int i) {
            return (new ResultableThunk<Lock>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getI2cWriteCacheLock(i);
                }
            }).doReadOperation();
        }

        @Override
        public byte[] getI2cReadCache(final int i) {
            return (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getI2cReadCache(i);
                }
            }).doReadOperation();
        }

        @Override
        public byte[] getI2cWriteCache(final int i) {
            return (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.getI2cWriteCache(i);
                }
            }).doReadOperation();
        }

        @Override
        public void setNxtI2cPortActionFlag(final int i) {
            (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.setNxtI2cPortActionFlag(i);
                }
            }).doWriteOperation();
        }

        @Override
        public boolean isNxtI2cPortActionFlagSet(final int i) {
            return (new ResultableThunk<Boolean>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.isNxtI2cPortActionFlagSet(i);
                }
            }).doReadOperation();
        }

        @Override
        public void readI2cCacheFromModule(final int i) {
            (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.readI2cCacheFromModule(i);
                }
            }).doWriteOperation();
        }

        @Override
        public void writeI2cCacheToModule(final int i) {
            (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.writeI2cCacheToModule(i);
                }
            }).doWriteOperation();
        }

        @Override
        public void writeI2cPortFlagOnlyToModule(final int i) {
            (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.writeI2cPortFlagOnlyToModule(i);
                }
            }).doWriteOperation();
        }

        @Override
        public boolean isI2cPortInReadMode(final int i) {
            return (new ResultableThunk<Boolean>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.isI2cPortInReadMode(i);
                }
            }).doReadOperation();
        }

        @Override
        public boolean isI2cPortInWriteMode(final int i) {
            return (new ResultableThunk<Boolean>()
            {
                @Override protected void actionOnLoopThread()
                {
                    this.result = target.isI2cPortInWriteMode(i);
                }
            }).doReadOperation();
        }

        @Override
        public void registerForPortReadyCallback(final PortReadyCallback portReadyCallback, final int i) {
            (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.registerForPortReadyCallback(portReadyCallback, i);
                }
            }).doWriteOperation();
        }

        @Override
        public void deregisterForPortReadyCallback(final int i) {
            (new ResultableThunk<byte[]>()
            {
                @Override protected void actionOnLoopThread()
                {
                    target.deregisterForPortReadyCallback(i);
                }
            }).doWriteOperation();
        }

        @Override public boolean isI2cPortReady(final int physicalPort)
        {
        return (new ResultableThunk<Boolean>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.isI2cPortReady(physicalPort);
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
