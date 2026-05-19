package manager;

import appservice.RestaurantAppService;
import dataservice.RestaurantDataService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import model.RestaurantModel;
import user.AbstractAppService;

public class ReportService extends AbstractAppService {

    private final RestaurantDataService.IOrder _order;
    private final RestaurantDataService.IPayment _payment;
    private final IInventoryItem _inventoryItem;
    private final IFoodWaste _foodWaste;

    private static final int EXPIRY_THRESHOLD_DAYS = 7;
    private static final String TOTAL_REVENUE = "totalRevenue";
    private static final String TOTAL_TIPS = "totalTips";
    private static final String TRANSACTION_COUNT = "transactionCount";

    public ReportService(RestaurantDataService.IOrder order, RestaurantDataService.IPayment payment, IInventoryItem inventoryItem, IFoodWaste foodWaste){
        this._order = order;
        this._payment = payment;
        this._inventoryItem = inventoryItem;
        this._foodWaste = foodWaste;
    }

    // report completed payments for the date in revenue, tips, count, average, and method breakdown
    public HashMap<String, Object> dailySalesReport(LocalDate reportDate){
        HashMap<String, Object> reportData = new HashMap<>();
        HashMap<String, Long> paymentMethodBreakdown = new HashMap<>();

        double totalRevenue = 0.0;
        double totalTips = 0.0;
        int transactionCount = 0;

        for(RestaurantModel.Payment currentPayment : _payment.findByDate(reportDate)){
            if(RestaurantAppService.PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                transactionCount++;
                totalRevenue += currentPayment.getPaymentAmount();
                totalTips += currentPayment.getPaymentTipAmount();

                String paymentMethod = currentPayment.getPaymentMethod();
                Long methodCount = paymentMethodBreakdown.get(paymentMethod);
                if(methodCount == null){
                    paymentMethodBreakdown.put(paymentMethod, 1L);
                }else{
                    paymentMethodBreakdown.put(paymentMethod, methodCount + 1);
                }
            }
        }
        double averageTransaction = transactionCount > 0 ? totalRevenue / transactionCount : 0.0;
        reportData.put("date", reportDate);
        reportData.put(TOTAL_REVENUE, totalRevenue);
        reportData.put(TOTAL_TIPS, totalTips);
        reportData.put(TRANSACTION_COUNT, transactionCount);
        reportData.put("averageTransaction", averageTransaction);
        reportData.put("paymentMethodBreakdown", paymentMethodBreakdown);
        return reportData;
    }

    // report total items, low stock count, expiring soon count, total value
    public HashMap<String, Object> inventoryReport(){
        HashMap<String, Object> reportData = new HashMap<>();
        HashMap<String, Long> categoryCountBreakdown = new HashMap<>();

        ArrayList<InventoryItem> allInventoryItems = _inventoryItem.getAll();
        ArrayList<InventoryItem> lowStockInventoryItems = _inventoryItem.findLowStock();
        ArrayList<InventoryItem> expiringSoonInventoryItems = _inventoryItem.findExpiringSoon(EXPIRY_THRESHOLD_DAYS);

        double totalInventoryValue = 0.0;
        for(InventoryItem currentInventoryItem : allInventoryItems){
            totalInventoryValue += currentInventoryItem.getInventoryQuantity() * currentInventoryItem.getInventoryCostPerUnit();

            String itemCategory = currentInventoryItem.getInventoryCategory();
            Long categoryCount = categoryCountBreakdown.get(itemCategory);
            if(categoryCount == null){
                categoryCountBreakdown.put(itemCategory, 1L);
            }else{
                categoryCountBreakdown.put(itemCategory, categoryCount + 1);
            }
        }
        reportData.put("totalItems", allInventoryItems.size());
        reportData.put("lowStockCount", lowStockInventoryItems.size());
        reportData.put("expiringSoonCount", expiringSoonInventoryItems.size());
        reportData.put("totalInventoryValue", totalInventoryValue);
        reportData.put("categoryBreakdown", categoryCountBreakdown);
        reportData.put("lowStockItems", lowStockInventoryItems);
        reportData.put("expiringSoonItems", expiringSoonInventoryItems);
        return reportData;
    }

    // report waste records in date range with cost totals, reason counts, and category costs
    public HashMap<String, Object> wasteReport(LocalDate reportStartDate, LocalDate reportEndDate){
        HashMap<String, Object> reportData = new HashMap<>();
        HashMap<String, Long> wasteReasonBreakdown = new HashMap<>();
        HashMap<String, Double> wasteCategoryBreakdown = new HashMap<>();

        double totalWasteCost = 0.0;
        ArrayList<FoodWaste> wasteRecordsInRange = _foodWaste.findByDateRange(reportStartDate, reportEndDate);

        for(FoodWaste currentWasteRecord : wasteRecordsInRange){
            totalWasteCost += currentWasteRecord.getFoodWasteEstimatedCost();

            String wasteReason = currentWasteRecord.getFoodWasteReason();
            Long reasonCount = wasteReasonBreakdown.get(wasteReason);
            if(reasonCount == null){
                wasteReasonBreakdown.put(wasteReason, 1L);
            }else{
                wasteReasonBreakdown.put(wasteReason, reasonCount + 1);
            }

            String wasteCategory = currentWasteRecord.getFoodWasteCategory();
            Double categoryCost = wasteCategoryBreakdown.get(wasteCategory);
            if(categoryCost == null){
                wasteCategoryBreakdown.put(wasteCategory, currentWasteRecord.getFoodWasteEstimatedCost());
            }else{
                wasteCategoryBreakdown.put(wasteCategory, categoryCost + currentWasteRecord.getFoodWasteEstimatedCost());
            }
        }
        reportData.put("startDate", reportStartDate);
        reportData.put("endDate", reportEndDate);
        reportData.put("totalWasteRecords", wasteRecordsInRange.size());
        reportData.put("totalWasteCost", totalWasteCost);
        reportData.put("reasonBreakdown", wasteReasonBreakdown);
        reportData.put("categoryBreakdown", wasteCategoryBreakdown);
        return reportData;
    }

    // report all orders for status counts, completed count, total revenue, and average order value
    public HashMap<String, Object> orderReport(){
        HashMap<String, Object> reportData = new HashMap<>();
        HashMap<String, Long> orderStatusBreakdown = new HashMap<>();
        ArrayList<RestaurantModel.Order> allRestaurantOrders = _order.getAll();

        for(RestaurantModel.Order currentOrder : allRestaurantOrders){
            String orderStatus = currentOrder.getOrderStatus();
            Long statusCount = orderStatusBreakdown.get(orderStatus);
            if(statusCount == null){
                orderStatusBreakdown.put(orderStatus, 1L);
            }else{
                orderStatusBreakdown.put(orderStatus, statusCount + 1);
            }
        }

        ArrayList<RestaurantModel.Order> completedRestaurantOrders = _order.findByStatus(RestaurantAppService.OrderStatus.COMPLETED);
        double totalCompletedRevenue = 0.0;
        for(RestaurantModel.Order completedOrder : completedRestaurantOrders){
            totalCompletedRevenue += completedOrder.getOrderTotalAmount();
        }

        double averageOrderValue = completedRestaurantOrders.isEmpty() ? 0.0 : totalCompletedRevenue / completedRestaurantOrders.size();
        reportData.put("totalOrders", allRestaurantOrders.size());
        reportData.put("completedOrders", completedRestaurantOrders.size());
        reportData.put("totalRevenue", totalCompletedRevenue);
        reportData.put("averageOrderValue", averageOrderValue);
        reportData.put("statusBreakdown", orderStatusBreakdown);
        return reportData;
    }

    // returns summary for active orders, completed orders, total revenue, low stock alerts
    public HashMap<String, Object> dashboardSummary(){
        HashMap<String, Object> summaryData = new HashMap<>();
        ArrayList<RestaurantModel.Order> allRestaurantOrders = _order.getAll();

        long activeOrderCount = 0;
        long completedOrderCount = 0;
        double totalRevenueFromCompletedOrders = 0.0;

        for(RestaurantModel.Order currentOrder : allRestaurantOrders){
            String orderStatus = currentOrder.getOrderStatus();
            if(!RestaurantAppService.OrderStatus.COMPLETED.equals(orderStatus)
                && !RestaurantAppService.OrderStatus.CANCELLED.equals(orderStatus)){
                activeOrderCount++;
            }
            if(RestaurantAppService.OrderStatus.COMPLETED.equals(orderStatus)){
                completedOrderCount++;
                totalRevenueFromCompletedOrders += currentOrder.getOrderTotalAmount();
            }
        }
        int lowStockAlertCount = _inventoryItem.findLowStock().size();
        summaryData.put("totalRevenue", totalRevenueFromCompletedOrders);
        summaryData.put("activeOrders", activeOrderCount);
        summaryData.put("totalOrders", completedOrderCount);
        summaryData.put("lowStockAlerts", lowStockAlertCount);
        return summaryData;
    }

    // combine daily sales and waste reports for the entire month
    public HashMap<String, Object> monthlyReport(int reportYear, int reportMonth){
        LocalDate monthStartDate = LocalDate.of(reportYear, reportMonth, 1);
        LocalDate monthEndDate = monthStartDate.withDayOfMonth(monthStartDate.lengthOfMonth());

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("year", reportYear);
        reportData.put("month", reportMonth);
        reportData.put("salesData", dailySalesReport(monthStartDate));
        reportData.put("wasteData", wasteReport(monthStartDate, monthEndDate));
        return reportData;
    }
}
