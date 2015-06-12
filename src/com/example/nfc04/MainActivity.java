package com.example.nfc04;

import java.util.Arrays;

import com.example.nfc04.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView mTextView;
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) this.findViewById(R.id.t);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter != null) {
			mTextView.setText("Selecciona un producto!");
		} else {
			mTextView.setText("No esta activado el NFC!");
		}

		// Crea un intent con datos de la etiqueta y la entrega a esta activity
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Configura un intent filter para todos los datos MIME
		IntentFilter ndefIntent = new IntentFilter(	NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] { ndefIntent };
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}

		mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
	}

	@Override
	public void onNewIntent(Intent intent) {
		AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);

		String s = "";

		// analiza a traves de todos los mensajes NDEF y sus registros y obtiene solo los tipo texto
		Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (data != null) {
			try {
				for (int i = 0; i < data.length; i++) {
					NdefRecord[] recs = ((NdefMessage) data[i]).getRecords();
					for (int j = 0; j < recs.length; j++) {
						if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
							byte[] payload = recs[j].getPayload();
							String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
							int langCodeLen = payload[0] & 0077;
							s = (new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,	textEncoding));
						}
					}
				}
			} catch (Exception e) {
				Log.e("TagDispatch", e.toString());
			}
		}

		mTextView.setText(s);
		if (s.equals("Perro" )|| s.equals("perro" )) {			

			dialogo1.setTitle("Importante");
			dialogo1.setMessage("¿Desea ordenar un perro?");
			dialogo1.setCancelable(false);
			dialogo1.setPositiveButton("Confirmar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo1, int id) {
							aceptar();
						}
					});
			dialogo1.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo1, int id) {
							cancelar();
						}
					});
			dialogo1.show();
		}
		else if(s.equals("Hamburguesa" )|| s.equals("hamburguesa" )){
			dialogo1.setTitle("Importante");
			dialogo1.setMessage("¿Desea ordenar una hamburguesa?");
			dialogo1.setCancelable(false);
			dialogo1.setPositiveButton("Confirmar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo1, int id) {
							aceptar();
						}
					});
			dialogo1.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo1, int id) {
							cancelar();
						}
					});
			dialogo1.show();
		}
		else{
			Toast.makeText(this, "No es una etiqueta reconocible!",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void aceptar() {
		Toast t = Toast.makeText(this, "Ordenado!",
				Toast.LENGTH_SHORT);
		t.show();
	}

	public void cancelar() {		
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if (mNfcAdapter != null)
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
					mIntentFilters, mNFCTechLists);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mNfcAdapter != null)
			mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
