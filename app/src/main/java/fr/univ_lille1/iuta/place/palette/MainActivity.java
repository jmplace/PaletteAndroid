package fr.univ_lille1.iut_info.place.palette;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private CharSequence[] colorNames;
    private int[] colors;
    private int color ;

    class Primary {
        private boolean fromRgb ;
        private int valeur ;
        private SeekBar seekBar ;
        private TextView textView;
        private boolean tracking ;
        private SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int valeur, boolean fromUser) {
                if (fromUser) {
                    Primary.this.valeur = valeur;
                    textView.setText("" + valeur);
                    updateColor(fromRgb);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        } ;
        public Primary(int seekBarId, int textViewId) {
            this(seekBarId, textViewId, true, 255) ;
        }
        public Primary(int seekBarId, int textViewId, boolean fromRgb, int max) {
            valeur = ((max+1) / 2) ;
            this.fromRgb = fromRgb ;
            tracking = false ;
            seekBar = (SeekBar) findViewById(seekBarId) ;
            textView = (TextView) findViewById(textViewId) ;
            seekBar.setMax(max) ;
            seekBar.setProgress(valeur);
            textView.setText("" + valeur) ;
            seekBar.setOnSeekBarChangeListener(listener);
        }
        public int getValeur() {
            return valeur ;
        }
        public float getFloatValeur() {
            return (float) valeur ;
        }
        public void setValue(int value) {
            this.valeur = value ;
            seekBar.setProgress(value) ;
            textView.setText("" + value) ;
        }
    }

    private Primary rouge ;
    private Primary vert ;
    private Primary bleu ;

    private Primary hue ;
    private Primary sat ;
    private Primary value ;

    private void setColor(int color) {
        Button button = (Button) findViewById(R.id.lookup) ;
        button.setBackgroundColor(color) ;
        rouge.setValue(Color.red(color));
        vert.setValue(Color.green(color));
        bleu.setValue(Color.blue(color)) ;
        float[] hsv = new float[3] ;
        Color.colorToHSV(color, hsv);
        hue.setValue((int) hsv[0]) ;
        sat.setValue((int) (hsv[1]*100)) ;
        value.setValue((int) (hsv[2]*100)) ;
    }
    public void copyColor(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner) ;
        int idx = spinner.getSelectedItemPosition() ;
        setColor(colors[idx]) ;
    }
    private void updateColor(boolean fromRgb) {
        float[] hsv = new float[3] ;
        if (fromRgb) {
            setColor(color = Color.rgb(rouge.getValeur(), vert.getValeur(), bleu.getValeur())) ;
        } else {
            hsv[0] = (float) hue.getValeur() ;
            hsv[1] = (sat.getFloatValeur())/100 ;
            hsv[2] = (value.getFloatValeur())/100 ;
            setColor(Color.HSVToColor(hsv)) ;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorNames = this.getResources().getStringArray(R.array.color_names);
        colors = this.getResources().getIntArray(R.array.colors);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_names, R.layout.list_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                TextView tvTest = (TextView) findViewById(R.id.demo);
                tvTest.setText(colorNames[(int) id]);
                tvTest.setBackgroundColor(colors[(int) id]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                TextView tvTest = (TextView) findViewById(R.id.demo);
                tvTest.setText(R.string.color_name);
                tvTest.setBackgroundColor(getColor(R.color.defaut));
            }

        });

        rouge = new Primary(R.id.Rouge, R.id.EditRouge) ;
        vert = new Primary(R.id.Vert, R.id.EditVert) ;
        bleu = new Primary(R.id.Bleu, R.id.EditBleu) ;

        hue = new Primary(R.id.Hue, R.id.EditHue, false, 360) ;
        sat = new Primary(R.id.Saturation, R.id.EditSaturation, false, 100) ;
        value = new Primary(R.id.Lumin, R.id.EditLumin, false, 100) ;

        updateColor(false) ;
    }

    private int lookupColor() {
        int max = 65536 * 3 ;
        int found = 0 ;
        for (int i = 0 ; i < colorNames.length ; i++) {
            int r = rouge.getValeur() ;
            int v = vert.getValeur() ;
            int b = bleu.getValeur() ;
            int color = colors[i] ;
            int b1 = Color.blue(color) ;
            int v1 = Color.green(color) ;
            int r1 = Color.red(color) ;
            int distance = (r1-r)*(r1-r) + (v1-v)*(v1-v) + (b1-b)*(b1-b) ;
            if (distance < max) {
                max = distance ;
                found = i ;
            }
        }
        return found ;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Couleur", color) ;
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState) ;
        color = inState.getInt("Couleur") ;
        Button button = (Button) findViewById(R.id.lookup) ;
        button.setBackgroundColor(color) ;
    }
    public void recherche(View view) {
        int found = lookupColor() ;
        TextView tvTest = (TextView) findViewById(R.id.demo);
        tvTest.setText(colorNames[found]);
        tvTest.setBackgroundColor(colors[found]);
        Spinner spinner = (Spinner) findViewById(R.id.spinner) ;
        spinner.setSelection(found) ;
    }
}
package fr.univ_lille1.iuta.place.palette;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
