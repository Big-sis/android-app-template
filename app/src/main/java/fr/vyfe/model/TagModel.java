package fr.vyfe.model;


import android.os.Parcel;
import android.os.Parcelable;

public class TagModel implements Parcelable, VyfeModel {

    private String id;
    private ColorModel color;
    private String name;
    private String templateId;
    private String sessionId;
    private String taggerId;
    private int start;
    private int end;


    public TagModel() {
    }

    protected TagModel(Parcel in) {
        id = in.readString();
        templateId = in.readString();
        sessionId = in.readString();
        taggerId = in.readString();
        start = in.readInt();
        end = in.readInt();
    }


    public static final Creator<TagModel> CREATOR = new Creator<TagModel>() {
        @Override
        public TagModel createFromParcel(Parcel in) {
            return new TagModel(in);
        }

        @Override
        public TagModel[] newArray(int size) {
            return new TagModel[size];
        }
    };

    public static TagModel createFromTemplate(TemplateModel template) {
        TagModel newTagModel = new TagModel();
        newTagModel.setName(template.getName());
        newTagModel.setTemplateId(template.getId());
        newTagModel.setColor(template.getColor());
        return newTagModel;
    }

    public String getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(String taggerId) {
        this.taggerId = taggerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ColorModel getColor() {
        return color;
    }

    public void setColor(ColorModel color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(templateId);
        dest.writeString(sessionId);
        dest.writeString(taggerId);
        dest.writeInt(start);
        dest.writeInt(end);
    }
}


