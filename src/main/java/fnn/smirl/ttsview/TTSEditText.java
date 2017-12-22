package fnn.smirl.ttsview;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;
import java.util.Locale;
import java.util.*;

public class TTSEditText extends EditText {
  private Context mContext;
  private InnerTts innerTts;
  public TTSEditText(Context context) {
	super(context);
	mContext = context;
	innerTts = new InnerTts();
  }

  public TTSEditText(Context context, AttributeSet attrs) {
	super(context, attrs);
	mContext = context;
	innerTts = new InnerTts();
  }

  public void startReading() {
	startReading(null);
  }

  public boolean isReading() {
	return innerTts.isReading();
  }

  public void stopReading() {
	innerTts.stop();
  }

  public void startReading(OnReadingStateListener listener) {
	innerTts.read(getText().toString(), listener);
  }

  public void startReading(int queueMode, OnReadingStateListener listener) {
	innerTts.read(getText().toString(), queueMode, listener);
  }

  public ArrayList<Language> getAvailableLanguages() {
	ArrayList<Language> al = new ArrayList<Language>();
	for (Locale l : Locale.getAvailableLocales()){
	  Language la = new Language(l);
	if(la.name.length() > 0)al.add(la);
	}
	Collections.sort(al);
	return al;
  }

  public void setTextToSpeechLanguage(Language language) {
	innerTts.setLanguage(language.locale);
  }
  
  /*** the implementation of TextToSpeech */
  private  class InnerTts {

	private TextToSpeech tts;

	/** check reading state */
	public boolean isReading() {
	  return tts.isSpeaking();
	}

	public InnerTts() {
	  init();
	}

	/** initializing with french language */
	public void init() {
	  init(Locale.getDefault());
	}

	public void init(final Locale lang) {
	  tts = new TextToSpeech(mContext.getApplicationContext(),
		new TextToSpeech.OnInitListener() {
		  @Override
		  public void onInit(int status) {
			if (status != TextToSpeech.ERROR) {
			  // confugure how it will read
			  //pitch, rate, language, engine, ...
			  tts.setLanguage(lang);
			}}});

	}

	private void setLanguage(Locale locale) {
	  tts.setLanguage(locale);
	}

	public void read(String txt, OnReadingStateListener listener) {
	  read(txt, TextToSpeech.QUEUE_FLUSH, listener);
	}

	public void read(String txt, int queueMode, OnReadingStateListener listener) {
	  //you can use TextToSpeech.QUEUE_ADD 
	  //to append to the end of the queue
	  listenToEnd(listener);
	  tts.speak(txt, queueMode, null);
	}

	/*** stop reading text aloud */
	public void stop() {
	  tts.stop();
	}

	/*** close TTS and release resources */
	public void shutDown() {
	  stop(); tts.shutdown();
	}

	private void listenToEnd(final OnReadingStateListener listener) {
	  final Handler h = new Handler();
	  if (listener != null)listener.onReadingStarted();
	  h.postDelayed(new Runnable(){

		  @Override
		  public void run() {
			if (tts != null) {
			  if (listener != null && tts.isSpeaking())listener.onReadingProgressing();
			  if (tts.isSpeaking())h.postDelayed(this, 500);
			  else {
				h.removeCallbacks(this);
				if (listener != null)listener.onReadingEnded();
			  }
			} else {
			  if (listener != null)listener.onReadingFailed();
			}
		  }
		}, 1000);
	}
  }

  /** interface */
  public  interface OnReadingStateListener {
	void onReadingStarted();
	void onReadingProgressing();
	void onReadingEnded();
	void onReadingFailed();
  }

}
