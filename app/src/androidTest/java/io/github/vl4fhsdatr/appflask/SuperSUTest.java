package io.github.vl4fhsdatr.appflask;

import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SuperSUTest {

    private boolean commandIsFinished;
    private int commandExitCode;

    @Test
    public void testSuperSUAvailable() {
        if (Shell.SU.available()) {
            new Shell.Builder()
                    .useSU()
                    .setWantSTDERR(true)
                    .addCommand("pm path android", 0, new Shell.OnCommandResultListener() {
                        @Override
                        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                            commandIsFinished = true;
                            commandExitCode = exitCode;
                        }
                    })
                    .open();
            while (!commandIsFinished) {
                SystemClock.sleep(1000);
            }
            assertEquals(0, commandExitCode);
        } else {
            fail();
        }
    }

}
