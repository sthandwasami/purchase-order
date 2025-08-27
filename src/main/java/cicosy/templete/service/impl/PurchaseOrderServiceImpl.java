package cicosy.templete.service.impl;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.User;
import cicosy.templete.repository.PurchaseOrderRepository;
import cicosy.templete.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        // Logic to be implemented
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public PurchaseOrder approvePurchaseOrder(Long purchaseOrderId, User approver) {
        // Logic to be implemented
        return null;
    }

    @Override
    public PurchaseOrder rejectPurchaseOrder(Long purchaseOrderId, String reason, User approver) {
        // Logic to be implemented
        return null;
    }

    @Override
    public PurchaseOrder sendToSupplier(Long purchaseOrderId) {
        // Logic to be implemented
        return null;
    }

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Optional<PurchaseOrder> findById(Long id) {
        return purchaseOrderRepository.findById(id);
    }
}
