package com.team3824.akmessing1.scoutingapp.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.MessageType;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothSync {
    private static String TAG = "BluetoothSync";
    private static final String NAME_SECURE = "SyncSecure";
    private static final String NAME_INSECURE = "SyncInsecure";
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private boolean mSecure;

    private Handler mHandler;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public String connectedAddress = "";

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     */
    public BluetoothSync(Handler hander, boolean secure) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = hander;
        mSecure = secure;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Return the device id of the connected device
     */
    public synchronized String getConnectedAddress()
    {
        if(mState == STATE_CONNECTED)
        {
            return connectedAddress;
        }
        return "";
    }


    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(mSecure);
            mAcceptThread.start();
        }

        connectedAddress = "";
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);

        connectedAddress = "";
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, String socketType) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket,socketType);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
        connectedAddress = device.getAddress();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public boolean write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return false;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        return r.write(out);
    }

    /**
     * Write a file to the ConnectedThread in an unsynchronized manner
     *
     * @param file The buffered input file to write
     * @see ConnectedThread#writeFile(File)
     */
    public boolean writeFile(File file) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return false;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        return r.writeFile(file);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        Log.e(TAG, "Connection Failed");
        // Start the service over to restart listening mode
        BluetoothSync.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        Log.e(TAG,"Connection Lost");
        // Start the service over to restart listening mode
        BluetoothSync.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String socketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";
            // Create a new listening server socket
            try {
                if(secure)
                {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                            MY_UUID_SECURE);
                }
                else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE,
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket listen() failed", e);
            }
            mmServerSocket = tmp;
        }


        public void run() {
            Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread"+socketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothSync.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), socketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");

        }

        public void cancel() {
            Log.d(TAG, "Socket cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread"+socketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSync.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, socketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private int mSubstate;
        private static final int SUBSTATE_SENDING = 0;
        private static final int SUBSTATE_RECEIVING = 1;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mSubstate = SUBSTATE_RECEIVING;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            try {
                while (mState == STATE_CONNECTED) {
                    if (mmInStream.available() > 0 && mSubstate == SUBSTATE_RECEIVING) {

                        boolean waitingForHeader = true;
                        ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
                        byte[] headerBytes = new byte[22];
                        byte[] digest = new byte[16];
                        int headerIndex = 0;
                        byte header;
                        int totalSize = -1, remainingSize = -1;

                        while (true) {
                            if (waitingForHeader) {
                                header = (byte)mmInStream.read();
                                Log.v(TAG, "Received Header Byte: " + header);
                                headerBytes[headerIndex++] = header;

                                if (headerIndex == 22) {
                                    if ((headerBytes[0] == Constants.HEADER_MSB) && (headerBytes[1] == Constants.HEADER_LSB)) {
                                        Log.v(TAG, "Header Received.  Now obtaining length");
                                        byte[] dataSizeBuffer = Arrays.copyOfRange(headerBytes, 2, 6);
                                        totalSize = Utilities.byteArrayToInt(dataSizeBuffer);
                                        remainingSize = totalSize;
                                        Log.v(TAG, "Data size: " + totalSize);
                                        digest = Arrays.copyOfRange(headerBytes, 6, 22);
                                        waitingForHeader = false;
                                    } else {
                                        Log.e(TAG, "Did not receive correct header.  Closing socket");
                                        mmSocket.close();
                                        mHandler.sendEmptyMessage(MessageType.INVALID_HEADER);
                                        break;
                                    }
                                }

                            } else {
                                // Read the data from the stream in chunks
                                byte[] buffer = new byte[Constants.CHUNK_SIZE];
                                Log.v(TAG, String.format("Waiting for data.  Expecting %d more bytes.",remainingSize));
                                int bytesRead = mmInStream.read(buffer);
                                Log.v(TAG, "Read " + bytesRead + " bytes into buffer");
                                dataOutputStream.write(buffer, 0, bytesRead);
                                remainingSize -= bytesRead;

                                if (remainingSize <= 0) {
                                    Log.v(TAG, "Expected data has been received.");
                                    break;
                                }
                            }
                        }

                        // check the integrity of the data
                        final byte[] data = dataOutputStream.toByteArray();

                        if (Utilities.digestMatch(data, digest)) {
                            Log.v(TAG, "Digest matches OK.");
                            Message message = new Message();
                            message.obj = data;
                            message.what = MessageType.DATA_RECEIVED;
                            mHandler.sendMessage(message);

                            // Send the digest back to the client as a confirmation
                            Log.v(TAG, "Sending back digest for confirmation");
                            mmOutStream.write(digest);

                        } else {
                            Log.e(TAG, "Digest did not match.  Corrupt transfer?");
                            mHandler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
                        }

                    }
                    while (mmInStream.available() == 0 && mSubstate != SUBSTATE_RECEIVING);
                }
            } catch (IOException e) {
                Log.e(TAG,e.toString());
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public boolean write(byte[] buffer) {
            Log.d(TAG,"Sending: "+new String(buffer));
            mSubstate = SUBSTATE_SENDING;
            try {
                mHandler.sendEmptyMessage(MessageType.SENDING_DATA);

                // Send the header control first
                mmOutStream.write(Constants.HEADER_MSB);
                mmOutStream.write(Constants.HEADER_LSB);

                // write size
                mmOutStream.write(Utilities.intToByteArray(buffer.length));

                // write digest
                byte[] digest = Utilities.getDigest(buffer);
                mmOutStream.write(digest);

                // now write the data
                mmOutStream.write(buffer);
                mmOutStream.flush();

                Log.v(TAG, "Data sent.  Waiting for return digest as confirmation");

                byte[] incomingDigest = new byte[16];
                int incomingIndex = 0;

                try {
                    while (true) {
                        byte header = (byte)mmInStream.read();
                        incomingDigest[incomingIndex++] = header;
                        if (incomingIndex == 16) {
                            if (Utilities.digestMatch(buffer, incomingDigest)) {
                                Log.v(TAG, "Digest matched OK.  Data was received OK.");
                                mHandler.sendEmptyMessage(MessageType.DATA_SENT_OK);
                                mSubstate = SUBSTATE_RECEIVING;
                                return true;
                            } else {
                                Log.e(TAG, "Digest did not match.  Might want to resend.");
                                mHandler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
                                mSubstate = SUBSTATE_RECEIVING;
                                return false;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }

            }
            catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
            mSubstate = SUBSTATE_RECEIVING;
            return false;
        }

        /**
         * Write a file to the connect OutStream.
         *
         * @param file The buffered input stream from the file to write
         */
        public boolean writeFile(File file)
        {
            Log.d(TAG,"Sending file...");
            mSubstate = SUBSTATE_SENDING;

            try {
                mHandler.sendEmptyMessage(MessageType.SENDING_DATA);

                byte[] bytes = new byte[1024];
                int len;

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
                while(bis.available()>0)
                {
                    len = bis.read(bytes,0,bytes.length);
                    if(len <= 0)
                        break;
                    if(len == bytes.length) {
                        dataOutputStream.write(bytes);
                    }
                    else {
                        dataOutputStream.write(bytes,0,len);
                        break;
                    }
                }
                bis.close();

                byte[] buffer = dataOutputStream.toByteArray();

                // Send the header control first
                mmOutStream.write(Constants.HEADER_MSB);
                mmOutStream.write(Constants.HEADER_LSB);

                // write size
                mmOutStream.write(Utilities.intToByteArray(bis.available()));

                // write digest
                byte[] digest = Utilities.getDigest(buffer);
                mmOutStream.write(digest);

                // now write the data
                mmOutStream.write(buffer);
                mmOutStream.flush();

                Log.v(TAG, "Data sent.  Waiting for return digest as confirmation");

                byte[] incomingDigest = new byte[16];
                int incomingIndex = 0;

                while (true) {
                    byte header = (byte)mmInStream.read();
                    incomingDigest[incomingIndex++] = header;
                    if (incomingIndex == 16) {
                        if (Utilities.digestMatch(buffer, incomingDigest)) {
                            Log.v(TAG, "Digest matched OK.  Data was received OK.");
                            mHandler.sendEmptyMessage(MessageType.DATA_SENT_OK);
                            mSubstate = SUBSTATE_RECEIVING;
                            return true;
                        } else {
                            Log.e(TAG, "Digest did not match.  Might want to resend.");
                            mHandler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
                            mSubstate = SUBSTATE_RECEIVING;
                            return false;
                        }
                    }
                }

            }
            catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
            mSubstate = SUBSTATE_RECEIVING;
            return false;

        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }


}
