package ftc.team6460.javadeck.ftc.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import ftc.team6460.javadeck.api.Maintainable;
import ftc.team6460.javadeck.api.Maintainer;
import ftc.team6460.javadeck.ftc.peripheral.FtcPeripheralsFactory;
import org.swerverobotics.library.SynchronousOpMode;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hexafraction on 6/11/15.
 */
public abstract class FtcBaseOpMode extends SynchronousOpMode implements Maintainer {
    private final Set<Maintainable> toMaintain = new HashSet<>();

    /**
     * Needs to catch and handle InterruptedException.
     */
    protected abstract void doActions();

    protected volatile boolean run = false;

    protected FtcPeripheralsFactory peripheralFactory = new FtcPeripheralsFactory(super.hardwareMap, this);

    {

    }

    @Override
    public void accept(Maintainable m) {
        this.executeOnLoopThread(m::loop);
    }


    @Override
    protected void postStartHook()  {
        for(Maintainable m : toMaintain){
            m.setup();
        }
    }

    @Override
    protected void main(){
        doActions();
    }
}
