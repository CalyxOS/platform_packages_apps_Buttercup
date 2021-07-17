package org.calyxos.buttercup.model;

import com.google.gson.annotations.SerializedName;

import org.calyxos.buttercup.model.compat.TicketArticleCompat;

import java.io.Serializable;

public class Ticket implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("group_id")
    private int groupId;

    @SerializedName("group")
    private String group;

    @SerializedName("state_id")
    private int stateId;

    @SerializedName("state")
    private String state;

    @SerializedName("priority_id")
    private int priorityId;

    @SerializedName("priority")
    private String priority;

    @SerializedName("customer_id")
    private int customerId;

    @SerializedName("customer")
    private String customer;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("owner")
    private String owner;

    @SerializedName("article")
    private TicketArticleCompat article;

    @SerializedName("note")
    private String note;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_by_id")
    private int createdById;

    @SerializedName("updated_by_id")
    private int updatedById;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("updated_by")
    private String updatedBy;

    public Ticket() {
    }

    public Ticket(int id, String title, int groupId, String group, int stateId, String state, int priorityId, String priority,
                  int customerId, String customer, int ownerId, String owner, TicketArticleCompat article, String note, String createdAt,
                  String updatedAt, int createdById, int updatedById, String createdBy, String updatedBy) {
        this.id = id;
        this.title = title;
        this.groupId = groupId;
        this.group = group;
        this.stateId = stateId;
        this.state = state;
        this.priorityId = priorityId;
        this.priority = priority;
        this.customerId = customerId;
        this.customer = customer;
        this.ownerId = ownerId;
        this.owner = owner;
        this.article = article;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdById = createdById;
        this.updatedById = updatedById;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public TicketArticleCompat getArticle() {
        return article;
    }

    public void setArticle(TicketArticleCompat article) {
        this.article = article;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public int getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(int updatedById) {
        this.updatedById = updatedById;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", groupId=" + groupId +
                ", group='" + group + '\'' +
                ", stateId=" + stateId +
                ", state='" + state + '\'' +
                ", priorityId=" + priorityId +
                ", priority='" + priority + '\'' +
                ", customerId=" + customerId +
                ", customer='" + customer + '\'' +
                ", ownerId=" + ownerId +
                ", owner='" + owner + '\'' +
                ", article=" + article +
                ", note='" + note + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdById=" + createdById +
                ", updatedById=" + updatedById +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
