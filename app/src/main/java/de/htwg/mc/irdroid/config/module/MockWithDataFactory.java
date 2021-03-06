package de.htwg.mc.irdroid.config.module;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.htwg.mc.irdroid.config.Factory;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.repository.mock.MockConnector;
import de.htwg.mc.irdroid.database.implementation.repository.mock.MockRepository;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.CommandType;
import de.htwg.mc.irdroid.model.Device;

/**
 * Provide access to the Repository mock implementations with dummy data.
 */
public class MockWithDataFactory implements Factory {
    private final MockConnector connector;
    private final Repository<Device> deviceRepository;

    public MockWithDataFactory(Context context) {
        connector = new MockConnector(context);
        deviceRepository = new MockRepository<>(connector, Device.class);

        /**
         * Paste any dummy data here.
         */

        // login as the default user, so that the user is the owner of these documents.
        connector.login("user", "pw");

        generateDummyCommands();

        // logout here
        connector.logout();
    }

    @Override
    public boolean login(String email, String password) {
        return connector.login(email, password);
    }

    @Override
    public boolean logout() {
        return connector.logout();
    }

    @Override
    public Repository<Device> provideDevice() {
        return deviceRepository;
    }

    public void generateDummyCommands() {

        String samsungPower = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015"
                + " 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015"
                + " 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015"
                + " 0015 0015 0040 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702"
                + " 00a9 00a8 0015 0015 0015 0e6e";

        String samsungVolUp = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015" +
                " 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8" +
                " 0015 0015 0015 0e6e";

        Device samsung = deviceRepository.create(new Device("Samsung"));
        addCommand(samsung, CommandType.power, samsungPower);
        addCommand(samsung, CommandType.volumeUp, samsungVolUp);

        /*
        Hitachi Beamer G151 ;-)
        String power = ""+
        poweron:  "0000 006d 0022 0002 0156 00aa 0016 0040 0016 0040 0016 0040 0016 0016 0016 0016 0016 0016 0016 0016 0016 0040 0016 0040 0016 0016 0016 0040 0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0040 0016 0016 0016 0040 0016 0040 0016 0040 0016 0016 0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0016 0016 0016 0016 0040 0016 0040 0016 0040 0016 0638 0156 0055 0016 0e64";
        */
        String hitachiPoweroff = "0000 006d 0022 0002 0156 00aa 0016 0040 0016 0040 0016 0040 " +
                "0016 0016 0016 0016 0016 0016 0016 0016 0016 0040 0016 0040 0016 0016 0016 0040 " +
                "0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0016 0016 0040 0016 0040 " +
                "0016 0040 0016 0040 0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0016 " +
                "0016 0016 0016 0016 0016 0040 0016 0040 0016 0040 0016 0638 0156 0055 0016 0e64";

        Device hitachi = deviceRepository.create(new Device("Hitachi Beamer"));
        addCommand(hitachi, CommandType.power, hitachiPoweroff);


       /*deviceRepository.create(new Device("Sony TV"));
        deviceRepository.create(new Device("LG TV"));
        deviceRepository.create(new Device("Beamer"));*/

    }

    private void addCommand(Device device, CommandType commandType, String commandString) {
        List<String> list = new ArrayList<>(Arrays.asList(commandString.split(" ")));
        list.remove(0); // dummy
        int frequency = (int) (1000000 / ((Integer.parseInt(list.remove(0), 16) * 0.241246)));
        list.remove(0); // seq1
        list.remove(0); // seq2

        int[] pattern = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            pattern[i] = Integer.decode("0x" + (list.get(i)));
        }

        Command command = new Command(frequency, pattern);
        device.addCommand(commandType, command);

        deviceRepository.update(device);
    }
}
