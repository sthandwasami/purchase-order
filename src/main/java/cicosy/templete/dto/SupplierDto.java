package cicosy.templete.dto;

import cicosy.templete.domain.Supplier;

public class SupplierDto {

    private Long id;
    private String name;
    private String category;
    private String contactInfo;

    public SupplierDto(Supplier supplier) {
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.category = supplier.getCategory();
        this.contactInfo = supplier.getContactInfo();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}
