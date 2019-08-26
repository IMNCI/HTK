package org.ministryofhealth.healthtalkkit.model;

public class HealthTalkKit {
    private int id, health_talk_kit_id, subcontent_id, is_parent, order;
    private String title, content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHealth_talk_kit_id() {
        return health_talk_kit_id;
    }

    public void setHealth_talk_kit_id(int health_talk_kit_id) {
        this.health_talk_kit_id = health_talk_kit_id;
    }

    public int getSubcontent_id() {
        return subcontent_id;
    }

    public void setSubcontent_id(int subcontent_id) {
        this.subcontent_id = subcontent_id;
    }

    public int getIs_parent() {
        return is_parent;
    }

    public void setIs_parent(int is_parent) {
        this.is_parent = is_parent;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
