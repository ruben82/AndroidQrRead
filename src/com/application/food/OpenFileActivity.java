package com.application.food;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.food.R;
import com.application.food.component.ActionBar;
import com.application.food.component.ActionBar;
import com.application.food.component.MenuActivity;
import com.application.food.component.MenuActivity;
import com.application.food.lib.pagesview.FindResult;
import com.application.food.lib.pagesview.PagesView;
import com.application.food.lib.pdf.PDF;
import com.application.food.model.DocumentWT;
import com.application.food.utils.Actions;
import com.application.food.utils.DBUtils;
import com.application.food.lib.pagesview.FindResult;
import com.application.food.lib.pagesview.PagesView;
import com.application.food.lib.pdf.PDF;
import com.application.food.utils.FileSystemUtils;
import com.application.food.utils.PDFPagesProvider;
import com.application.food.model.DocumentWT;
import com.application.food.utils.Actions;
import com.application.food.utils.DBUtils;
import com.application.food.utils.FileSystemUtils;
import com.application.food.utils.PDFPagesProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

// #ifdef pro
// import java.util.Stack;
// import java.util.Map;
// import android.content.DialogInterface.OnDismissListener;
// import android.view.ViewGroup.LayoutParams;
// import android.widget.ScrollView;
// import android.text.method.ScrollingMovementMethod;
// import cx.hell.android.lib.pdf.PDF.Outline;
// import cx.hell.android.lib.view.TreeView;
// #endif


/**
 * Document display activity.
 */
@SuppressWarnings("unused")
public class OpenFileActivity extends MenuActivity implements SensorEventListener, CustomMenu.OnMenuItemSelectedListener{

    private final static String TAG = "com.eni.icteam.doctra";
    private final static int[] zoomAnimations = {R.anim.zoom_disappear, R.anim.zoom_almost_disappear, R.anim.zoom};
    private final static int[] pageNumberAnimations = {R.anim.page_disappear, R.anim.page_almost_disappear, R.anim.page,R.anim.page_show_always};
    private PDF pdf = null;
    private PagesView pagesView = null;
    private PDFPagesProvider pdfPagesProvider = null;
    private Actions actions = null;

    private Handler zoomHandler = null;
    private Handler pageHandler = null;
    private Runnable zoomRunnable = null;
    private Runnable pageRunnable = null;

    private MenuItem aboutMenuItem = null;
    private MenuItem gotoPageMenuItem = null;
    private MenuItem rotateLeftMenuItem = null;
    private MenuItem rotateRightMenuItem = null;
    private MenuItem findTextMenuItem = null;
    private MenuItem clearFindTextMenuItem = null;
    private MenuItem chooseFileMenuItem = null;
    private MenuItem optionsMenuItem = null;
    private EditText pageNumberInputField = null;
    private EditText findTextInputField = null;
    private LinearLayout findButtonsLayout = null;
    private Button findPrevButton = null;
    private Button findNextButton = null;
    private Button findHideButton = null;
    private LinearLayout activityLayout = null;
    private RelativeLayout innerLayout = null;
    private boolean eink = false;

    // currently opened file path
    private String filePath = "/";

    private String findText = null;
    private Integer currentFindResultPage = null;
    private Integer currentFindResultNumber = null;

    // zoom buttons, layout and fade animation
    private ImageButton zoomDownButton;
    private ImageButton zoomWidthButton;
    private ImageButton zoomUpButton;
    private Animation zoomAnim;
    private LinearLayout zoomLayout;
    private ActionBar bar;

    // page number display
    private TextView pageNumberTextView;
    private Animation pageNumberAnim;

    private int box = 2;

    public boolean showZoomOnScroll = false;

    private int fadeStartOffset = 7000;

    private int colorMode = Options.COLOR_MODE_NORMAL;

    private SensorManager sensorManager;
    private static final int ZOOM_COLOR_NORMAL = 0;
    private static final int ZOOM_COLOR_RED = 1;
    private static final int ZOOM_COLOR_GREEN = 2;
    private static final int[] zoomUpId = {
            R.drawable.btn_zoom_up, R.drawable.red_btn_zoom_up, R.drawable.green_btn_zoom_up
    };
    private static final int[] zoomDownId = {
            R.drawable.btn_zoom_down, R.drawable.red_btn_zoom_down, R.drawable.green_btn_zoom_down
    };
    private static final int[] zoomWidthId = {
            R.drawable.btn_zoom_width, R.drawable.red_btn_zoom_width, R.drawable.green_btn_zoom_width
    };
    private float[] gravity = { 0f, -9.81f, 0f};

    private int prevOrientation;

    private boolean history = true;
    private DBUtils dbUtils;
    private boolean locale=false;
    private DocumentWT documentWT;

    private final  Activity activity=this;

    private String completeUrl;
    private String titleDocument;
    private ImageView vv;
    private ImageView action1;
    /*
  private boolean toSave=false;
  private boolean toDelete=false;*/
// #ifdef pro
// 	/**
// 	 * If true, then current activity is in text reflow mode.
// 	 */
// 	private boolean textReflowMode = false;
// #endif

    /**
     * Called when the activity is first created.
     * TODO: initialize dialog fast, then move file loading to other thread
     * TODO: add progress bar for file load
     * TODO: add progress icon for file rendering
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Options.setOrientation(this);
        SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(this);


        completeUrl=getIntent().getExtras().getString("completeUrl");
        titleDocument=getIntent().getExtras().getString("title");
        dbUtils= DBUtils.getInstance(this.getApplicationContext());
        documentWT=dbUtils.getDocument(titleDocument);

        locale=getIntent().getExtras().getBoolean("library");
        this.box = Integer.parseInt(options.getString(Options.PREF_BOX, "2"));

        LayoutInflater mInflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Get display metrics
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // use a relative layout to stack the views
        activityLayout = new LinearLayout(this);
        innerLayout = new RelativeLayout(this);
        activityLayout.setOrientation(LinearLayout.VERTICAL);

        // the PDF view
        this.pagesView = new PagesView(this);


        mMenu = new CustomMenu(this, this, getLayoutInflater());
        mMenu.setHideOnSelect(true);
        mMenu.setItemsPerLineInPortraitOrientation(4);
        mMenu.setItemsPerLineInLandscapeOrientation(8);
        this.view=pagesView;
        this.codeActivity= 5;// non appare nel menu
        loadMenuItems();


        bar = new ActionBar(this,null);
        bar.setLayoutParams(new RelativeLayout.LayoutParams(
               RelativeLayout.LayoutParams.FILL_PARENT,55));

        action1 = (ImageView) bar.findViewById(R.id.action_one);
        Drawable action1_draw = getResources().getDrawable((locale) ? R.drawable.delete : R.drawable.save);
        action1.setImageDrawable(action1_draw);
        action1.setVisibility((locale || documentWT==null) ? View.VISIBLE : View.GONE);
        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrDeleteFile();
            }
        });

        activityLayout.addView(bar);
        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(  new ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));

        //mlp.setMargins(0, 50, 0, 0);
        l.setVerticalScrollBarEnabled(true);
        l.addView(pagesView,mlp);
        innerLayout.addView(l);
        startPDF(options);
        if (!this.pdf.isValid()) {
            finish();
        }
        // the find buttons
        this.findButtonsLayout = new LinearLayout(this);
        this.findButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.findButtonsLayout.setVisibility(View.GONE);
        this.findButtonsLayout.setGravity(Gravity.CENTER);
        this.findPrevButton = new Button(this);
        this.findPrevButton.setText(R.string.prev);
        this.findButtonsLayout.addView(this.findPrevButton);
        this.findNextButton = new Button(this);
        this.findNextButton.setText(R.string.next);
        this.findButtonsLayout.addView(this.findNextButton);
        this.findHideButton = new Button(this);
        this.findHideButton.setText(R.string.hide);
        this.findButtonsLayout.addView(this.findHideButton);
        this.setFindButtonHandlers();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        innerLayout.addView(this.findButtonsLayout, lp);

        this.pageNumberTextView = new TextView(this);
        this.pageNumberTextView.setTextSize(8f*metrics.density);
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        innerLayout.addView(this.pageNumberTextView, lp);

        // display this
        activityLayout.addView(innerLayout);
        this.setContentView(activityLayout);

        // go to last viewed page
//        gotoLastPage();

        // send keyboard events to this view
        pagesView.setFocusable(true);
        pagesView.setFocusableInTouchMode(true);

        this.zoomHandler = new Handler();
        this.pageHandler = new Handler();
        this.zoomRunnable = new Runnable() {
            public void run() {
                fadeZoom();
            }
        };
        this.pageRunnable = new Runnable() {
            public void run() {
                fadePage();
            }
        };
    }

    private void saveOrDeleteFile() {
        if (locale) {
            boolean delete = FileSystemUtils.deleteFile(activity, documentWT);
            Toast.makeText(activity, getString(R.string.delete_done), Toast.LENGTH_SHORT).show();
            action1.setVisibility(View.GONE);
        } else if (!locale && documentWT==null) {
            action1.setVisibility(View.GONE);
            savingOnLibrary();
            Toast.makeText(activity, getString(R.string.save_done), Toast.LENGTH_SHORT).show();
        }
    }

   public void savingOnLibrary(){

        File  pathLocale= new File(Environment.getExternalStorageDirectory() + "/wt/tmp");
        String date="";
        String nameFile;
       
        try{
            FileInputStream fis = new FileInputStream(completeUrl);
            String path= FileSystemUtils.createDocumentFolder()+"/"+titleDocument;
            InputStream input = new BufferedInputStream(fis);
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            DocumentWT d= new DocumentWT();
            d.title=titleDocument;
            d.path=path;
            DBUtils.getInstance(activity).addDocument(d);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, getString(R.string.error_savin_library), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save the current page before exiting
     */
    @Override
    protected void onPause() {
        super.onPause();

        //	saveLastPage();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putInt(Options.PREF_PREV_ORIENTATION, prevOrientation);
            edit.commit();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        FileSystemUtils.cleanTmp();
    }
    @Override
    protected void onResume() {
        super.onResume();

        sensorManager = null;

        SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(this);

        if (Options.setOrientation(this)) {
            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) {
                gravity[0] = 0f;
                gravity[1] = -9.81f;
                gravity[2] = 0f;
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
                this.prevOrientation = options.getInt(Options.PREF_PREV_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        history  = options.getBoolean(Options.PREF_HISTORY, true);
        boolean eink = options.getBoolean(Options.PREF_EINK, false);
        this.pagesView.setEink(eink);
        if (eink)
            this.setTheme(android.R.style.Theme_Light);
        this.pagesView.setNook2(options.getBoolean(Options.PREF_NOOK2, false));

        if (options.getBoolean(Options.PREF_KEEP_ON, false))
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        actions = new Actions(options);
        this.pagesView.setActions(actions);

        setZoomLayout(options);

        this.pagesView.setZoomLayout(zoomLayout);

        this.showZoomOnScroll = options.getBoolean(Options.PREF_SHOW_ZOOM_ON_SCROLL, false);
        this.pagesView.setSideMargins(
                Integer.parseInt(options.getString(Options.PREF_SIDE_MARGINS, "0")));
        this.pagesView.setTopMargin(
                Integer.parseInt(options.getString(Options.PREF_TOP_MARGIN, "0")));

        this.pagesView.setDoubleTap(Integer.parseInt(options.getString(Options.PREF_DOUBLE_TAP,
                ""+Options.DOUBLE_TAP_ZOOM_IN_OUT)));

        int newBox = Integer.parseInt(options.getString(Options.PREF_BOX, "2"));
        if (this.box != newBox) {
            //	saveLastPage();
            this.box = newBox;
            startPDF(options);
            //this.pagesView.goToBookmark();
        }

        this.colorMode = Options.getColorMode(options);
        this.eink = options.getBoolean(Options.PREF_EINK, false);
        this.pageNumberTextView.setBackgroundColor(Options.getBackColor(colorMode));
        this.pageNumberTextView.setTextColor(Options.getForeColor(colorMode));
        this.pdfPagesProvider.setGray(Options.isGray(this.colorMode));
        this.pdfPagesProvider.setExtraCache(1024*1024*Options.getIntFromString(options, Options.PREF_EXTRA_CACHE, 0));
        this.pdfPagesProvider.setOmitImages(options.getBoolean(Options.PREF_OMIT_IMAGES, false));
        this.pagesView.setColorMode(this.colorMode);

        this.pdfPagesProvider.setRenderAhead(options.getBoolean(Options.PREF_RENDER_AHEAD, true));
        this.pagesView.setVerticalScrollLock(options.getBoolean(Options.PREF_VERTICAL_SCROLL_LOCK, false));
        this.pagesView.invalidate();
        int zoomAnimNumber = Integer.parseInt(options.getString(Options.PREF_ZOOM_ANIMATION, "2"));

        if (zoomAnimNumber == Options.ZOOM_BUTTONS_DISABLED)
            zoomAnim = null;
        else
            zoomAnim = AnimationUtils.loadAnimation(this,
                    zoomAnimations[zoomAnimNumber]);
        int pageNumberAnimNumber = Integer.parseInt(options.getString(Options.PREF_PAGE_ANIMATION, "3"));

        if (pageNumberAnimNumber == Options.PAGE_NUMBER_DISABLED)
            pageNumberAnim = null;
        else
            pageNumberAnim = AnimationUtils.loadAnimation(this,
                    pageNumberAnimations[pageNumberAnimNumber]);

        fadeStartOffset = 1000 * Integer.parseInt(options.getString(Options.PREF_FADE_SPEED, "7"));

        if (options.getBoolean(Options.PREF_FULLSCREEN, false))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.pageNumberTextView.setVisibility(pageNumberAnim == null ? View.GONE : View.VISIBLE);
// #ifdef pro
// 		this.zoomLayout.setVisibility((zoomAnim == null || this.textReflowMode) ? View.GONE : View.VISIBLE);
// #endif

// #ifdef lite
        this.zoomLayout.setVisibility(zoomAnim == null ? View.GONE : View.VISIBLE);
// #endif

        showAnimated(true);
    }

    /**
     * Set handlers on findNextButton and findHideButton.
     */
    private void setFindButtonHandlers() {
        this.findPrevButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                OpenFileActivity.this.findPrev();
            }
        });
        this.findNextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                OpenFileActivity.this.findNext();
            }
        });
        this.findHideButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                OpenFileActivity.this.findHide();
            }
        });
    }

    /**
     * Set handlers on zoom level buttons
     */
    private void setZoomButtonHandlers() {
        this.zoomDownButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                pagesView.doAction(actions.getAction(Actions.LONG_ZOOM_IN));
                return true;
            }
        });
        this.zoomDownButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pagesView.doAction(actions.getAction(Actions.ZOOM_IN));
            }
        });
        this.zoomWidthButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pagesView.zoomWidth();
            }
        });
        this.zoomWidthButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                pagesView.zoomFit();
                return true;
            }
        });
        this.zoomUpButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pagesView.doAction(actions.getAction(Actions.ZOOM_OUT));
            }
        });
        this.zoomUpButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                pagesView.doAction(actions.getAction(Actions.LONG_ZOOM_OUT));
                return true;
            }
        });
    }

    private void startPDF(SharedPreferences options) {
        this.pdf = this.getPDF();
        if (!this.pdf.isValid()) {
            Log.v(TAG, "Invalid PDF");
            if (this.pdf.isInvalidPassword()) {
                Toast.makeText(this, getString(R.string.pdf_password), 4000).show();
            }
            else {
                Toast.makeText(this, getString(R.string.pdf_invalid), 4000).show();
            }
          //  saveOrDeleteFile();
            return;
        }
        this.colorMode = Options.getColorMode(options);
        this.pdfPagesProvider = new PDFPagesProvider(this, pdf,
                Options.isGray(this.colorMode),
                options.getBoolean(Options.PREF_OMIT_IMAGES, false),
                options.getBoolean(Options.PREF_RENDER_AHEAD, true));
        pagesView.setPagesProvider(pdfPagesProvider);
        /* Bookmark b = new Bookmark(this.getApplicationContext()).open();
          pagesView.setStartBookmark(b, filePath);
          b.close();*/
    }

    /**
     * Return PDF instance wrapping file referenced by Intent.
     * Currently reads all bytes to memory, in future local files
     * should be passed to native code and remote ones should
     * be downloaded to local tmp dir.
     * @return PDF instance
     */
    private PDF getPDF() {
        final Intent intent = getIntent();
        Uri uri = intent.getData();
        filePath = uri.getPath();
        if (uri.getScheme().equals("file")) {
            /*if (history) {
                   Recent recent = new Recent(this);
                   recent.add(0, filePath);
                   recent.commit();
               }*/
            return new PDF(new File(filePath), this.box);
        } else if (uri.getScheme().equals("content")) {
            ContentResolver cr = this.getContentResolver();
            FileDescriptor fileDescriptor;
            try {
                fileDescriptor = cr.openFileDescriptor(uri, "r").getFileDescriptor();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e); // TODO: handle errors
            }
            return new PDF(fileDescriptor, this.box);
        } else {
            throw new RuntimeException("don't know how to get filename from " + uri);
        }
    }

    private void setOrientation(int orientation) {
        if (orientation != this.prevOrientation) {
            setRequestedOrientation(orientation);
            this.prevOrientation = orientation;
        }
    }



    /**
     * Intercept touch events to handle the zoom buttons animation
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            showPageNumber(true);
            if (showZoomOnScroll) {
                showZoom();
            }
        }
        return super.dispatchTouchEvent(event);
    };

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (action == KeyEvent.ACTION_UP || action == KeyEvent.ACTION_DOWN) {
            if (!eink)
                showAnimated(false);
        }
        return super.dispatchKeyEvent(event);
    };

    public void showZoom() {
// #ifdef pro
//     	if (this.textReflowMode) {
//     		zoomLayout.setVisibility(View.GONE);
//     		return;
//     	}
// #endif
        if (zoomAnim == null) {
            zoomLayout.setVisibility(View.GONE);
            return;
        }

        zoomLayout.clearAnimation();
        zoomLayout.setVisibility(View.VISIBLE);
        zoomHandler.removeCallbacks(zoomRunnable);
        zoomHandler.postDelayed(zoomRunnable, fadeStartOffset);
    }

    private void fadeZoom() {
// #ifdef pro
//     	if (this.textReflowMode) {
//     		this.zoomLayout.setVisibility(View.GONE);
//     		return;
//     	}
// #endif
        if (eink || zoomAnim == null) {
            zoomLayout.setVisibility(View.GONE);
        }
        else {
            zoomAnim.setStartOffset(0);
            zoomAnim.setFillAfter(true);
            zoomLayout.startAnimation(zoomAnim);
        }
    }

    public void showPageNumber(boolean force) {
        if (pageNumberAnim == null) {
            pageNumberTextView.setVisibility(View.GONE);
            return;
        }

        pageNumberTextView.setVisibility(View.VISIBLE);
        String newText = ""+(this.pagesView.getCurrentPage()+1)+"/"+
                this.pdfPagesProvider.getPageCount();

        if (!force && newText.equals(pageNumberTextView.getText()))
            return;

        pageNumberTextView.setText(newText);
        pageNumberTextView.clearAnimation();

        pageHandler.removeCallbacks(pageRunnable);
        pageHandler.postDelayed(pageRunnable, fadeStartOffset);
    }

    private void fadePage() {
        if (eink || pageNumberAnim == null) {
            pageNumberTextView.setVisibility(View.GONE);
        }
        else {
            pageNumberAnim.setStartOffset(0);
            pageNumberAnim.setFillAfter(true);
            pageNumberTextView.startAnimation(pageNumberAnim);
        }
    }

    /**
     * Show zoom buttons and page number
     */
    public void showAnimated(boolean alsoZoom) {
        if (alsoZoom)
            showZoom();
        showPageNumber(true);
    }

    /**
     * Hide the find buttons
     */
    private void clearFind() {
        this.currentFindResultPage = null;
        this.currentFindResultNumber = null;
        this.pagesView.setFindMode(false);
        this.findButtonsLayout.setVisibility(View.GONE);
    }

    /**
     * Show error message to user.
     * @param message message to show
     */
    private void errorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage(message).setTitle("Error").create();
        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged(" + newConfig + ")");
    }

    /**
     * Show find dialog.
     * Very pretty UI code ;)
     */
    public void showFindDialog() {
        Log.d(TAG, "find dialog...");
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.find_dialog_title);
        LinearLayout contents = new LinearLayout(this);
        contents.setOrientation(LinearLayout.VERTICAL);
        this.findTextInputField = new EditText(this);
        this.findTextInputField.setWidth(this.pagesView.getWidth() * 80 / 100);
        Button goButton = new Button(this);
        goButton.setText(R.string.find_go_button);
        goButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = OpenFileActivity.this.findTextInputField.getText().toString();
                OpenFileActivity.this.findText(text);
                dialog.dismiss();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 5;
        params.rightMargin = 5;
        params.bottomMargin = 2;
        params.topMargin = 2;
        contents.addView(findTextInputField, params);
        contents.addView(goButton, params);
        dialog.setContentView(contents);
        dialog.show();
    }

    private void setZoomLayout(SharedPreferences options) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int colorMode = Options.getColorMode(options);
        int mode = ZOOM_COLOR_NORMAL;

        if (colorMode == Options.COLOR_MODE_GREEN_ON_BLACK) {
            mode = ZOOM_COLOR_GREEN;
        }
        else if (colorMode == Options.COLOR_MODE_RED_ON_BLACK) {
            mode = ZOOM_COLOR_RED;
        }

        // the zoom buttons
        if (zoomLayout != null) {
            innerLayout.removeView(zoomLayout);
        }

        zoomLayout = new LinearLayout(this);
        zoomLayout.setOrientation(LinearLayout.HORIZONTAL);
        zoomDownButton = new ImageButton(this);
        zoomDownButton.setImageDrawable(getResources().getDrawable(zoomDownId[mode]));
        zoomDownButton.setBackgroundColor(Color.TRANSPARENT);
        zoomLayout.addView(zoomDownButton, (int)(80 * metrics.density), (int)(50 * metrics.density));	// TODO: remove hardcoded values
        zoomWidthButton = new ImageButton(this);
        zoomWidthButton.setImageDrawable(getResources().getDrawable(zoomWidthId[mode]));
        zoomWidthButton.setBackgroundColor(Color.TRANSPARENT);
        zoomLayout.addView(zoomWidthButton, (int)(58 * metrics.density), (int)(50 * metrics.density));
        zoomUpButton = new ImageButton(this);
        zoomUpButton.setImageDrawable(getResources().getDrawable(zoomUpId[mode]));
        zoomUpButton.setBackgroundColor(Color.TRANSPARENT);
        zoomLayout.addView(zoomUpButton, (int)(80 * metrics.density), (int)(50 * metrics.density));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setZoomButtonHandlers();
        innerLayout.addView(zoomLayout,lp);
    }

    private void findText(String text) {
        Log.d(TAG, "findText(" + text + ")");
        this.findText = text;
        this.find(true);
    }

    /**
     * Called when user presses "next" button in find panel.
     */
    private void findNext() {
        this.find(true);
    }

    /**
     * Called when user presses "prev" button in find panel.
     */
    private void findPrev() {
        this.find(false);
    }

    /**
     * Called when user presses hide button in find panel.
     */
    private void findHide() {
        if (this.pagesView != null) this.pagesView.setFindMode(false);
        this.currentFindResultNumber = null;
        this.currentFindResultPage = null;
        this.findButtonsLayout.setVisibility(View.GONE);
    }


    /**
     * Helper class that handles search progress, search cancelling etc.
     */
    static class Finder implements Runnable, DialogInterface.OnCancelListener, DialogInterface.OnClickListener {
        private OpenFileActivity parent = null;
        private boolean forward;
        private AlertDialog dialog = null;
        private String text;
        private int startingPage;
        private int pageCount;
        private boolean cancelled = false;
        /**
         * Constructor for finder.
         * @param parent parent activity
         */
        public Finder(OpenFileActivity parent, boolean forward) {
            this.parent = parent;
            this.forward = forward;
            this.text = parent.findText;
            this.pageCount = parent.pagesView.getPageCount();
            if (parent.currentFindResultPage != null) {
                if (forward) {
                    this.startingPage = (parent.currentFindResultPage + 1) % pageCount;
                } else {
                    this.startingPage = (parent.currentFindResultPage - 1 + pageCount) % pageCount;
                }
            } else {
                this.startingPage = parent.pagesView.getCurrentPage();
            }
        }
        public void setDialog(AlertDialog dialog) {
            this.dialog = dialog;
        }
        public void run() {
            int page = -1;
            this.createDialog();
            this.showDialog();
            for(int i = 0; i < this.pageCount; ++i) {
                if (this.cancelled) {
                    this.dismissDialog();
                    return;
                }
                page = (startingPage + pageCount + (this.forward ? i : -i)) % this.pageCount;
                Log.d(TAG, "searching on " + page);
                this.updateDialog(page);
                List<FindResult> findResults = this.findOnPage(page);
                if (findResults != null && !findResults.isEmpty()) {
                    Log.d(TAG, "found something at page " + page + ": " + findResults.size() + " results");
                    this.dismissDialog();
                    this.showFindResults(findResults, page);
                    return;
                }
            }
            /* TODO: show "nothing found" message */
            this.dismissDialog();
        }
        /**
         * Called by finder thread to get find results for given page.
         * Routed to PDF instance.
         * If result is not empty, then finder loop breaks, current find position
         * is saved and find results are displayed.
         * @param page page to search on
         * @return results
         */
        private List<FindResult> findOnPage(int page) {
            if (this.text == null) throw new IllegalStateException("text cannot be null");
            return this.parent.pdf.find(this.text, page);
        }
        private void createDialog() {
            this.parent.runOnUiThread(new Runnable() {
                public void run() {
                    String title = Finder.this.parent.getString(R.string.searching_for).replace("%1$s", Finder.this.text);
                    String message = Finder.this.parent.getString(R.string.page_of).replace("%1$d", String.valueOf(Finder.this.startingPage)).replace("%2$d", String.valueOf(pageCount));
                    AlertDialog.Builder builder = new AlertDialog.Builder(Finder.this.parent);
                    AlertDialog dialog = builder
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(true)
                            .setNegativeButton(R.string.cancel, Finder.this)
                            .create();
                    dialog.setOnCancelListener(Finder.this);
                    Finder.this.dialog = dialog;
                }
            });
        }
        public void updateDialog(final int page) {
            this.parent.runOnUiThread(new Runnable() {
                public void run() {
                    String message = Finder.this.parent.getString(R.string.page_of).replace("%1$d", String.valueOf(page)).replace("%2$d", String.valueOf(pageCount));
                    Finder.this.dialog.setMessage(message);
                }
            });
        }
        public void showDialog() {
            this.parent.runOnUiThread(new Runnable() {
                public void run() {
                    Finder.this.dialog.show();
                }
            });
        }
        public void dismissDialog() {
            final AlertDialog dialog = this.dialog;
            this.parent.runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });
        }
        public void onCancel(DialogInterface dialog) {
            Log.d(TAG, "onCancel(" + dialog + ")");
            this.cancelled = true;
        }
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "onClick(" + dialog + ")");
            this.cancelled = true;
        }
        private void showFindResults(final List<FindResult> findResults, final int page) {
            this.parent.runOnUiThread(new Runnable() {
                public void run() {
                    int fn = Finder.this.forward ? 0 : findResults.size()-1;
                    Finder.this.parent.currentFindResultPage = page;
                    Finder.this.parent.currentFindResultNumber = fn;
                    Finder.this.parent.pagesView.setFindResults(findResults);
                    Finder.this.parent.pagesView.setFindMode(true);
                    Finder.this.parent.pagesView.scrollToFindResult(fn);
                    Finder.this.parent.findButtonsLayout.setVisibility(View.VISIBLE);
                    Finder.this.parent.pagesView.invalidate();
                }
            });
        }
    };

    /**
     * GUI for finding text.
     * Used both on initial search and for "next" and "prev" searches.
     * Displays dialog, handles cancel button, hides dialog as soon as
     * something is found.
     * @param
     */
    private void find(boolean forward) {
        if (this.currentFindResultPage != null) {
            /* searching again */
            int nextResultNum = forward ? this.currentFindResultNumber + 1 : this.currentFindResultNumber - 1;
            if (nextResultNum >= 0 && nextResultNum < this.pagesView.getFindResults().size()) {
                /* no need to really find - just focus on given result and exit */
                this.currentFindResultNumber = nextResultNum;
                this.pagesView.scrollToFindResult(nextResultNum);
                this.pagesView.invalidate();
                return;
            }
        }

        /* finder handles next/prev and initial search by itself */
        Finder finder = new Finder(this, forward);
        Thread finderThread = new Thread(finder);
        finderThread.start();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        gravity[0] = 0.8f * gravity[0] + 0.2f * event.values[0];
        gravity[1] = 0.8f * gravity[1] + 0.2f * event.values[1];
        gravity[2] = 0.8f * gravity[2] + 0.2f * event.values[2];

        float sq0 = gravity[0]*gravity[0];
        float sq1 = gravity[1]*gravity[1];
        float sq2 = gravity[2]*gravity[2];

        if (sq1 > 3 * (sq0 + sq2)) {
            if (gravity[1] > 4)
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else if (gravity[1] < -4 && Integer.parseInt(Build.VERSION.SDK) >= 9)
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
        else if (sq0 > 3 * (sq1 + sq2)) {
            if (gravity[0] > 4)
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else if (gravity[0] < -4 && Integer.parseInt(Build.VERSION.SDK) >= 9)
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

}