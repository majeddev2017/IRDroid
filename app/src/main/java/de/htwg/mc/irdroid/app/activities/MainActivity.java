package de.htwg.mc.irdroid.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.List;
import java.util.Map;

import de.htwg.mc.irdroid.R;
import de.htwg.mc.irdroid.app.IrController;
import de.htwg.mc.irdroid.app.bluetooth.ManageDeviceActivity;
import de.htwg.mc.irdroid.config.Provider;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.specification.DeviceAllSpecification;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.CommandType;
import de.htwg.mc.irdroid.model.Device;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private IrController ir;

    private Map<CommandType, Command> commandMap;
    private Repository<Device> deviceRepository;
    private List<Device> devices;
    private ImageButton bPower;
    private ImageButton bVolumeUp;
    private ImageButton bVolumeDown;
    private ImageButton bChannelUp;
    private ImageButton bChannelDown;
    private ImageButton bDigits;
    private Device device;
    private Spinner deviceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ir = new IrController(this);

        bPower = (ImageButton) findViewById(R.id.bPower);
        bPower.setOnClickListener(this);
        bVolumeUp = (ImageButton) findViewById(R.id.bVolUp);
        bVolumeUp.setOnClickListener(this);
        bVolumeDown = (ImageButton) findViewById(R.id.bVolDown);
        bVolumeDown.setOnClickListener(this);
        bChannelUp = (ImageButton) findViewById(R.id.bChannelUp);
        bChannelUp.setOnClickListener(this);
        bChannelDown = (ImageButton) findViewById(R.id.bChannelDown);
        bChannelDown.setOnClickListener(this);
        bDigits = (ImageButton) findViewById(R.id.bDigits);
        bDigits.setOnClickListener(this);
        deviceSpinner = (Spinner) findViewById(R.id.spinner_devices);
        deviceSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceRepository = Provider.getInstance().getFactory().provideDevice();
        updateView();
    }

    private void updateView() {
        List<Device> tDevices = deviceRepository.read(new DeviceAllSpecification());
        Log.i(TAG, "Devices available :" + tDevices.size());

//        if (tDevices.isEmpty()) {
//            Provider.getInstance().getFactory().generateDummyCommands();
//            tDevices = deviceRepository.read(new DeviceAllSpecification());
//        }

        if (devices == null || devices.size() != tDevices.size()) {
            devices = tDevices;
            ArrayAdapter<Device> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, devices);
            //adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            deviceSpinner.setAdapter(adapter);
            device = (Device) deviceSpinner.getSelectedItem();
        }

        if (device == null)
            return;
        commandMap = device.getCommandMap();

        bPower.setEnabled(commandMap.containsKey(CommandType.power));
        bVolumeUp.setEnabled(commandMap.containsKey(CommandType.volumeUp));
        bVolumeDown.setEnabled(commandMap.containsKey(CommandType.volumeDown));
        bChannelUp.setEnabled(commandMap.containsKey(CommandType.channelUp));
        bChannelDown.setEnabled(commandMap.containsKey(CommandType.channelDown));
        bDigits.setEnabled(commandMap.containsKey(CommandType.digits));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(MainActivity.this, ManageDeviceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Command command = null;
        switch (v.getId()) {
            case R.id.bPower:
                command = device.getCommand(CommandType.power);
                break;
            case R.id.bVolUp:
                command = device.getCommand(CommandType.volumeUp);
                break;
            case R.id.bVolDown:
                command = device.getCommand(CommandType.volumeDown);
                break;
            case R.id.bChannelUp:
                command = device.getCommand(CommandType.channelUp);
                break;
            case R.id.bChannelDown:
                command = device.getCommand(CommandType.channelDown);
                break;
            case R.id.bDigits:
                showDigitsField();
                break;
            default:
                break;
        }

        if (command != null) {
            ir.sendCode(command.getFrequency(), command.getIrCommand());
        } else {
            Log.w(TAG, "unknown command");
        }
    }

    public void logDevices(View view) {
        Repository<Device> deviceRepository = Provider.getInstance().getFactory().provideDevice();
        List<Device> devices = deviceRepository.read(new DeviceAllSpecification());
        for (Device device : devices) {
            Log.i("Model", device.getName());
        }
    }

    private void showDigitsField() {
        Command command = commandMap.get(CommandType.digits);
        Log.w(TAG, "to be implemented: show digit field");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        device = (Device) parent.getSelectedItem();
        Log.d(TAG, "selected device " + device.getName());
        updateView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        device = null;
        Log.d(TAG, "no device selected");
        updateView();
    }
}
