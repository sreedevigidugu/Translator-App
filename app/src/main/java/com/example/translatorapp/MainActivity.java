package com.example.translatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainActivity extends AppCompatActivity {

    private Spinner fromspinner, tospinner;
    private EditText editText;
    private Button btn;
    private TextView translatedtv;

    String[] fromlanguages = {
            "from", "English", "Hindi", "Telugu", "Spanish", "French", "Arabic", "Urdu","Korean"
    };

    String[] Tolanguages = {
            "to", "English", "Hindi", "Telugu", "Spanish", "French", "Arabic", "Urdu","Korean"
    };

    String fromLanguageCode, toLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromspinner = findViewById(R.id.spinner1);
        tospinner = findViewById(R.id.spinner2);
        editText = findViewById(R.id.edittext);
        btn = findViewById(R.id.button);
        translatedtv = findViewById(R.id.translatedTv);

        fromspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                fromLanguageCode = GetLanguageCode(fromlanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromadapter = new ArrayAdapter(this, R.layout.spinner_item, fromlanguages);
        fromadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromspinner.setAdapter(fromadapter);

        tospinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = GetLanguageCode(Tolanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter Toadapter = new ArrayAdapter(this, R.layout.spinner_item, Tolanguages);
        Toadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tospinner.setAdapter(Toadapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedtv.setText("");
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Your Text", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Select Source Language", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Select Target Language", Toast.LENGTH_SHORT).show();
                } else {
                    TranslateText(fromLanguageCode, toLanguageCode, editText.getText().toString());
                }
            }
        });

        registerForContextMenu(translatedtv);
    }

    private void TranslateText(String fromLanguageCode, String toLanguageCode, String src) {
        translatedtv.setText("Downloading Language Model");
        try {
            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setSourceLanguage(fromLanguageCode)
                    .setTargetLanguage(toLanguageCode)
                    .build();
            Translator translator = Translation.getClient(options);
            DownloadConditions conditions = new DownloadConditions.Builder().build();
            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    translatedtv.setText("Translating....");
                    translator.translate(src).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            translatedtv.setText(s);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to download the language", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String GetLanguageCode(String language) {
        String LanguageCode;
        switch (language) {
            case "English":
                LanguageCode = TranslateLanguage.ENGLISH;
                break;
            case "Hindi":
                LanguageCode = TranslateLanguage.HINDI;
                break;
            case "Telugu":
                LanguageCode = TranslateLanguage.TELUGU;
                break;
            case "Spanish":
                LanguageCode = TranslateLanguage.SPANISH;
                break;
            case "French":
                LanguageCode = TranslateLanguage.FRENCH;
                break;
            case "Urdu":
                LanguageCode = TranslateLanguage.URDU;
                break;
            case "Korean":
                LanguageCode =TranslateLanguage.KOREAN;
                break;
            default:
                LanguageCode = "";
        }
        return LanguageCode;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options");
        menu.add(Menu.NONE, 0, Menu.NONE, "Copy");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("translatedText", translatedtv.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
