package com.pxmart.permission.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permission_row")
public class PermissionRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private PermissionApplication application;

    private String localIp;
    private String localDb;
    private String remoteIp;
    private String remoteDb;
    private String objectType;

    @Column(length = 500)
    private String objectName;

    private boolean canSelect;
    private boolean canInsert;
    private boolean canUpdate;
    private boolean canDelete;
    private boolean canExecute;
    private boolean canAlter;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PermissionApplication getApplication() { return application; }
    public void setApplication(PermissionApplication application) { this.application = application; }

    public String getLocalIp() { return localIp; }
    public void setLocalIp(String localIp) { this.localIp = localIp; }

    public String getLocalDb() { return localDb; }
    public void setLocalDb(String localDb) { this.localDb = localDb; }

    public String getRemoteIp() { return remoteIp; }
    public void setRemoteIp(String remoteIp) { this.remoteIp = remoteIp; }

    public String getRemoteDb() { return remoteDb; }
    public void setRemoteDb(String remoteDb) { this.remoteDb = remoteDb; }

    public String getObjectType() { return objectType; }
    public void setObjectType(String objectType) { this.objectType = objectType; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    public boolean isCanSelect() { return canSelect; }
    public void setCanSelect(boolean canSelect) { this.canSelect = canSelect; }

    public boolean isCanInsert() { return canInsert; }
    public void setCanInsert(boolean canInsert) { this.canInsert = canInsert; }

    public boolean isCanUpdate() { return canUpdate; }
    public void setCanUpdate(boolean canUpdate) { this.canUpdate = canUpdate; }

    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }

    public boolean isCanExecute() { return canExecute; }
    public void setCanExecute(boolean canExecute) { this.canExecute = canExecute; }

    public boolean isCanAlter() { return canAlter; }
    public void setCanAlter(boolean canAlter) { this.canAlter = canAlter; }
}
