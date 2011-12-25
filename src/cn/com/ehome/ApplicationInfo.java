package cn.com.ehome;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ApplicationInfo {
	
    /**
     * The application name.
     */
    public CharSequence title;

    /**
     * The intent used to start the application.
     */
    public Intent intent;
    
    public String packageName;


    /**
     * The application icon.
     */
    public Drawable iconBitmap;

    public ComponentName componentName;
    /**
     * When set to true, indicates that the icon has been resized.
     */
    public boolean filtered;
    public int     id=-1;

    /**
     * Creates the application intent based on a component name and various launch flags.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */

    public final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }
    
    public ApplicationInfo() {
    }
    
    public ApplicationInfo(Intent it) {
    	intent = new Intent(it);
    }
    public ApplicationInfo(ApplicationInfo info) {
        //super(info);
        componentName = info.componentName;
        title = info.title.toString();
        iconBitmap=info.iconBitmap;
        intent = new Intent(info.intent);
        id=info.id;
        packageName = info.packageName;
    }
   
    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationInfo)) {
            return false;
        }

        ApplicationInfo that = (ApplicationInfo) o;
        return title.equals(that.title) &&
                intent.getComponent().getClassName().equals(
                        that.intent.getComponent().getClassName());
    }

    
    public int hashCode() {
        int result;
        result = (title != null ? title.hashCode() : 0);
        final String name = intent.getComponent().getClassName();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
