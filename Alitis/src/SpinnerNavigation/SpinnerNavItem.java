package SpinnerNavigation;

/**
 * Create the model class for the spinner drop-down navigation.
 * This model class contains two elements, title and image for the list item.
 * (Image is not used on our implementation)
 */
public class SpinnerNavItem {
	
	private int title;
	private int icon;
     
    public SpinnerNavItem(int title, int icon){
        this.title = title;
        this.icon = icon;
    }
    
    public SpinnerNavItem(int title){
        this.title = title;
    }
     
    public int getIcon(){
        return this.icon;
    }

	public int getTitle() {
		return title;
	}

	public void seTtitle(int title) {
		this.title = title;
	}
}
