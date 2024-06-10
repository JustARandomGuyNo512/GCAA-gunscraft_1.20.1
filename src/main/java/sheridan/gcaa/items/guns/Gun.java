package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.BaseItem;

public class Gun extends BaseItem implements IGun {


    public Gun() {
        super(new Properties().stacksTo(1));
    }
}

