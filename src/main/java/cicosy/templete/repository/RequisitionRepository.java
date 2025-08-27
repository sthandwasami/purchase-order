package cicosy.templete.repository;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, Long> {
    List<Requisition> findByUser(User user);
    List<Requisition> findByDepartment(Department department);
    List<Requisition> findByStatus(Requisition.Status status);
}
