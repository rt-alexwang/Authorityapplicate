package com.pxmart.permission;

import com.pxmart.permission.entity.OptionItem;
import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.entity.PermissionApplication.ApplicationStatus;
import com.pxmart.permission.entity.PermissionRow;
import com.pxmart.permission.repository.OptionItemRepository;
import com.pxmart.permission.repository.PermissionApplicationRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    private final OptionItemRepository optionRepo;
    private final PermissionApplicationRepository appRepo;

    public DataInitializer(OptionItemRepository optionRepo, PermissionApplicationRepository appRepo) {
        this.optionRepo = optionRepo;
        this.appRepo = appRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        seedOptions();
        seedApplications();
    }

    // ─── 下拉選單預設值 ───────────────────────────────────────────
    private void seedOptions() {
        seedType("REQUEST_ITEM", new String[]{
            "申請帳號及DB物件權限",
            "申請系統存取權限",
            "申請報表查詢權限",
            "申請資料匯出權限",
            "申請管理員權限",
            "申請跨DB查詢權限"
        });

        seedType("DESCRIPTION", new String[]{
            "因業務需求，需存取相關資料表以進行日常作業",
            "因系統開發需求，需存取開發環境資料庫",
            "因資料分析需求，需具備查詢相關報表資料的權限",
            "因系統維護需求，需具備執行相關預存程序的權限",
            "因專案上線需求，需存取正式環境資料庫進行資料驗證",
            "因例行報表作業，需定期讀取銷售相關資料表"
        });

        seedType("LOCAL_SERVER", new String[]{
            "PXMART-RT",
            "PXMART-AP",
            "PXMART-DB01",
            "PXMART-DB02",
            "PXMART-RPT"
        });

        seedType("REMOTE_SERVER", new String[]{
            "192.168.100.199",
            "192.168.120.228",
            "192.168.120.229",
            "192.168.130.10",
            "192.168.130.11",
            "192.168.200.50"
        });

        seedType("DATABASE", new String[]{
            "UAC",
            "[RT_UMP]",
            "[RT_EDI]",
            "[MPX_MAIN]",
            "[MPX_LOG]",
            "[MPX_REPORT]",
            "[POS_MAIN]",
            "[POS_HIST]",
            "[WMS_MAIN]",
            "[CRM_MAIN]"
        });

        seedType("ACCOUNT", new String[]{
            "MPX_DEV_AP (PX-MART-RT DEV 帳號)",
            "MPX_UAT_AP (PXMART-RT UAT 帳號)",
            "hlchu (Henry)",
            "htwang (Lin)",
            "lcwei (Julia)",
            "jychen (Jason)",
            "smlin (Sam)",
            "ykwu (Yvonne)"
        });
    }

    // ─── 模擬申請單資料 ───────────────────────────────────────────
    private void seedApplications() {
        if (appRepo.count() > 0) return;

        // 申請單 1：已核准
        PermissionApplication a1 = newApp(
            "褚鴻霖",
            "申請帳號及DB物件權限",
            "因業務需求，需存取相關資料表以進行日常作業",
            "hlchu (Henry), MPX_DEV_AP (PX-MART-RT DEV 帳號), htwang (Lin), lcwei (Julia), MPX_UAT_AP (PXMART-RT UAT 帳號)",
            ApplicationStatus.APPROVED,
            "已確認業務需求，核准存取",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now().minusDays(8)
        );
        addRow(a1, "PXMART-RT", "",   "192.168.100.199", "UAC",       "SP",    "get.spUAC_SALES_INCENTIVE", false, false, false, false, true,  false);
        addRow(a1, "PXMART-RT", "",   "192.168.120.228", "[RT_UMP]",  "Table", "[dbo].[RT_UMP_ITEM]",       true,  false, false, false, false, false);
        addRow(a1, "PXMART-RT", "",   "192.168.120.228", "[RT_EDI]",  "View",  "[dbo].[vPXSellerBouns]",    true,  false, false, false, false, false);
        appRepo.save(a1);

        // 申請單 2：待審核
        PermissionApplication a2 = newApp(
            "王雅琪",
            "申請報表查詢權限",
            "因例行報表作業，需定期讀取銷售相關資料表",
            "ykwu (Yvonne)",
            ApplicationStatus.PENDING,
            null,
            LocalDateTime.now().minusDays(2),
            null
        );
        addRow(a2, "PXMART-RPT", "", "192.168.130.10", "[MPX_REPORT]", "View",  "[dbo].[vSalesDaily]",     true, false, false, false, false, false);
        addRow(a2, "PXMART-RPT", "", "192.168.130.10", "[MPX_REPORT]", "View",  "[dbo].[vSalesMonthly]",   true, false, false, false, false, false);
        addRow(a2, "PXMART-RPT", "", "192.168.130.10", "[MPX_REPORT]", "Table", "[dbo].[StoreSalesKPI]",   true, false, false, false, false, false);
        appRepo.save(a2);

        // 申請單 3：待審核
        PermissionApplication a3 = newApp(
            "陳俊宇",
            "申請系統開發DB存取權限",
            "因系統開發需求，需存取開發環境資料庫",
            "jychen (Jason), MPX_DEV_AP (PX-MART-RT DEV 帳號)",
            ApplicationStatus.PENDING,
            null,
            LocalDateTime.now().minusDays(1),
            null
        );
        addRow(a3, "PXMART-DB01", "", "192.168.120.228", "[MPX_MAIN]", "Table", "[dbo].[Member]",          true, true,  true,  false, false, false);
        addRow(a3, "PXMART-DB01", "", "192.168.120.228", "[MPX_MAIN]", "Table", "[dbo].[Order]",            true, true,  true,  false, false, false);
        addRow(a3, "PXMART-DB01", "", "192.168.120.228", "[MPX_MAIN]", "SP",    "[dbo].[sp_GetOrderList]", false, false, false, false, true,  false);
        addRow(a3, "PXMART-DB01", "", "192.168.120.228", "[MPX_LOG]",  "Table", "[dbo].[SystemLog]",       true, false, false, false, false, false);
        appRepo.save(a3);

        // 申請單 4：已拒絕
        PermissionApplication a4 = newApp(
            "林書民",
            "申請管理員權限",
            "因系統維護需求，需具備執行相關預存程序的權限",
            "smlin (Sam)",
            ApplicationStatus.REJECTED,
            "管理員權限需主管二階核准，請補附主管審核簽核單後重新申請",
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(4)
        );
        addRow(a4, "PXMART-DB02", "", "192.168.200.50", "[POS_MAIN]", "Table", "[dbo].[POS_TRANSACTION]",  true, true, true, true, false, true);
        addRow(a4, "PXMART-DB02", "", "192.168.200.50", "[POS_MAIN]", "Table", "[dbo].[POS_ITEM]",         true, true, true, true, false, true);
        appRepo.save(a4);

        // 申請單 5：已核准
        PermissionApplication a5 = newApp(
            "吳怡君",
            "申請跨DB查詢權限",
            "因資料分析需求，需具備查詢相關報表資料的權限",
            "jychen (Jason), smlin (Sam)",
            ApplicationStatus.APPROVED,
            "符合資料分析部門申請規範，核准",
            LocalDateTime.now().minusDays(15),
            LocalDateTime.now().minusDays(14)
        );
        addRow(a5, "PXMART-RPT", "", "192.168.130.11", "[CRM_MAIN]",  "View",  "[dbo].[vCustomerSegment]", true, false, false, false, false, false);
        addRow(a5, "PXMART-RPT", "", "192.168.130.11", "[CRM_MAIN]",  "Table", "[dbo].[CustomerProfile]",  true, false, false, false, false, false);
        addRow(a5, "PXMART-RPT", "", "192.168.130.10", "[WMS_MAIN]",  "View",  "[dbo].[vInventorySummary]",true, false, false, false, false, false);
        appRepo.save(a5);

        // 申請單 6：今日剛送出，待審核
        PermissionApplication a6 = newApp(
            "張雅惠",
            "申請資料匯出權限",
            "因專案上線需求，需存取正式環境資料庫進行資料驗證",
            "MPX_UAT_AP (PXMART-RT UAT 帳號), lcwei (Julia)",
            ApplicationStatus.PENDING,
            null,
            LocalDateTime.now().minusHours(2),
            null
        );
        addRow(a6, "PXMART-DB01", "", "192.168.120.229", "[POS_HIST]", "Table", "[dbo].[SalesHistory]",    true, false, false, false, false, false);
        addRow(a6, "PXMART-DB01", "", "192.168.120.229", "[POS_HIST]", "SP",    "[dbo].[sp_ExportSales]",  false, false, false, false, true, false);
        appRepo.save(a6);
    }

    // ─── Helper Methods ───────────────────────────────────────────
    private PermissionApplication newApp(
            String name, String item, String desc, String accounts,
            ApplicationStatus status, String comment,
            LocalDateTime createdAt, LocalDateTime reviewedAt) {
        PermissionApplication app = new PermissionApplication();
        app.setApplicantName(name);
        app.setRequestItem(item);
        app.setDescription(desc);
        app.setRequestAccounts(accounts);
        app.setStatus(status);
        app.setReviewerComment(comment);
        setCreatedAt(app, createdAt);
        app.setReviewedAt(reviewedAt);
        return app;
    }

    private void addRow(PermissionApplication app,
            String localIp, String localDb, String remoteIp, String remoteDb,
            String objectType, String objectName,
            boolean select, boolean insert, boolean update,
            boolean delete, boolean execute, boolean alter) {
        PermissionRow row = new PermissionRow();
        row.setApplication(app);
        row.setLocalIp(localIp);
        row.setLocalDb(localDb);
        row.setRemoteIp(remoteIp);
        row.setRemoteDb(remoteDb);
        row.setObjectType(objectType);
        row.setObjectName(objectName);
        row.setCanSelect(select);
        row.setCanInsert(insert);
        row.setCanUpdate(update);
        row.setCanDelete(delete);
        row.setCanExecute(execute);
        row.setCanAlter(alter);
        app.getRows().add(row);
    }

    private void setCreatedAt(PermissionApplication app, LocalDateTime dt) {
        try {
            Field f = PermissionApplication.class.getDeclaredField("createdAt");
            f.setAccessible(true);
            f.set(app, dt);
            Field u = PermissionApplication.class.getDeclaredField("updatedAt");
            u.setAccessible(true);
            u.set(app, dt);
        } catch (Exception ignored) {}
    }

    private void seedType(String type, String[] values) {
        if (optionRepo.existsByType(type)) return;
        List<OptionItem> items = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            items.add(new OptionItem(type, values[i], i + 1));
        }
        optionRepo.saveAll(items);
    }
}
