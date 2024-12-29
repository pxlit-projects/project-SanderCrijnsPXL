package be.pxl.repository;

import be.pxl.domain.Post;
import be.pxl.domain.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByStatus(ReviewStatus reviewStatus);
}
