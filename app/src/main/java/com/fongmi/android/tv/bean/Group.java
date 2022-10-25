package com.fongmi.android.tv.bean;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.fongmi.android.tv.utils.ImgUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Group {

    @SerializedName("channel")
    private List<Channel> channel;
    @SerializedName("icon")
    private String icon;
    @SerializedName("name")
    private String name;
    @SerializedName("pass")
    private String pass;

    private boolean selected;
    private int position;

    public Group(String name) {
        this.name = name;
        if (name.contains("_")) setPass(name.split("_")[1]);
    }

    public List<Channel> getChannel() {
        return channel = channel == null ? new ArrayList<>() : channel;
    }

    public void setChannel(List<Channel> channel) {
        this.channel = channel;
    }

    public String getIcon() {
        return TextUtils.isEmpty(icon) ? "" : icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return TextUtils.isEmpty(pass) ? "" : pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isHidden() {
        return !TextUtils.isEmpty(getPass());
    }

    public int getVisible() {
        return getIcon().isEmpty() ? View.GONE : View.VISIBLE;
    }

    public void loadIcon(ImageView view) {
        if (!getIcon().isEmpty()) ImgUtil.load(getIcon(), view);
    }

    public int find(int number) {
        return getChannel().lastIndexOf(Channel.create(number));
    }

    public int find(String name) {
        return getChannel().lastIndexOf(Channel.create(name));
    }

    public Channel next() {
        setPosition(getPosition() < getChannel().size() - 1 ? getPosition() + 1 : 0);
        return current();
    }

    public Channel prev() {
        setPosition(getPosition() > 0 ? getPosition() - 1 : getChannel().size() - 1);
        return current();
    }

    public Channel current() {
        return getChannel().get(getPosition()).group(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof Group)) return false;
        Group it = (Group) obj;
        return getName().equals(it.getName());
    }
}
