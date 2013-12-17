package com.application.food.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.application.food.CustomMenu;
import com.application.food.CustomMenu;
import com.application.food.CustomMenuItem;
import com.application.food.CustomMenuItem;
import com.application.food.LibraryActivity;
import com.application.food.LibraryActivity;
import com.application.food.MainActivity;
import com.application.food.R;
import com.application.food.MainActivity;
import com.application.food.utils.ActivityUtils;
import com.application.food.utils.ActivityUtils;
import com.application.food.zxing.client.android.CaptureActivity;
import com.application.food.zxing.client.android.CaptureActivity;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 18/04/13
 * Time: 10.11
 * To change this template use File | Settings | File Templates.
 */
public class MenuActivity extends Activity implements CustomMenu.OnMenuItemSelectedListener{

    protected CustomMenu mMenu;
    protected View view;
    protected int codeActivity;

    protected void loadMenuItems() {
        //This is kind of a tedious way to load up the menu items.
        //Am sure there is room for improvement.
        ArrayList<CustomMenuItem> menuItems = new ArrayList<CustomMenuItem>();
        CustomMenuItem cmi = new CustomMenuItem();
        cmi.setCaption(getString(R.string.menu_scrittura));
        cmi.setImageResourceId(R.drawable.ic_menu_library);
        cmi.setId(ActivityUtils.MENU_ITEM_1);
        menuItems.add(cmi);
        cmi = new CustomMenuItem();
        cmi.setCaption(getString(R.string.menu_scansione));
        cmi.setImageResourceId(R.drawable.qr_menu2);
        cmi.setId(ActivityUtils.MENU_ITEM_2);
        menuItems.add(cmi);
        cmi = new CustomMenuItem();
        cmi.setCaption(getString(R.string.menu_libreria));
        cmi.setImageResourceId(R.drawable.ic_menu_docs);
        cmi.setId(ActivityUtils.MENU_ITEM_3);
        menuItems.add(cmi);
        cmi = new CustomMenuItem();
        cmi.setCaption(getString(R.string.menu_close));
        cmi.setImageResourceId(R.drawable.quit);
        cmi.setId(ActivityUtils.MENU_ITEM_4);
        menuItems.add(cmi);
        if (!mMenu.isShowing())
            try {
                mMenu.setMenuItems(menuItems);
            } catch (Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Egads!");
                alert.setMessage(e.getMessage());
                alert.show();
            }
    }

    /**
     * Toggle our menu on user pressing the menu key.
     */
    private void doMenu() {
        if (mMenu.isShowing()) {
            mMenu.hide();
        } else {
            //Note it doesn't matter what widget you send the menu as long as it gets view.
            mMenu.show(view);
        }
    }

    /**
     * For the demo just toast the item selected.
     */
    @Override
    public void MenuItemSelectedEvent(CustomMenuItem selection) {
        if(selection.getId()==ActivityUtils.MENU_ITEM_1 && selection.getId()!=codeActivity)startActivity(new Intent(this, MainActivity.class));
        if(selection.getId()==ActivityUtils.MENU_ITEM_2 && selection.getId()!=codeActivity)startActivity(new Intent(this, CaptureActivity.class));
        if(selection.getId()==ActivityUtils.MENU_ITEM_3 && selection.getId()!=codeActivity)startActivity(new Intent(this, LibraryActivity.class));
        if(selection.getId()==ActivityUtils.MENU_ITEM_4 && selection.getId()!=codeActivity)
            ActivityUtils.callClosing(this);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            doMenu();
            return true; //always eat it!
        }
        return super.onKeyDown(keyCode, event);
    }
}
