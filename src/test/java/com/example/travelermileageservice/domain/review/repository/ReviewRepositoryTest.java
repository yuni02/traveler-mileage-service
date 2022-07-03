package com.example.travelermileageservice.domain.review.repository;

import com.example.travelermileageservice.config.JpaConfig;
import com.example.travelermileageservice.config.QueryDslConfig;
import com.example.travelermileageservice.domain.review.entity.AttachedPhoto;
import com.example.travelermileageservice.domain.review.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, JpaConfig.class})
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AttachedPhotoRepository attachedPhotoRepository;

    @DisplayName("저장")
    @Test
    void save() {
        // given
        final String content = "좋아요!";
        final UUID reviewId = UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772");
        final UUID userId = UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745");
        final UUID placeId = UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f");
        final List<AttachedPhoto> attachedPhotos = List.of(
                new AttachedPhoto(UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8")),
                new AttachedPhoto(UUID.fromString("afb0cef2-851d-4a50-bb07-9cc15cbdc332")));

        final Review review = new Review(reviewId, userId, content, placeId, attachedPhotos);

        // when
        reviewRepository.save(review);

        // then
        assertThat(review.getId()).isNotNull();
        assertThat(review.getPlaceId()).isEqualTo(placeId);
        attachedPhotos.stream()
                .map(photo -> attachedPhotoRepository.findByPhotoId(photo.getPhotoId()))
                .forEach(optionalPhoto -> assertThat(optionalPhoto).isNotNull());
    }

    @DisplayName("첨부 사진 삭제")
    @Test
    void deleteAttachedPhoto() {
        // given
        final String content = "좋아요!";
        final UUID reviewId = UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772");
        final UUID userId = UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745");
        final UUID placeId = UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f");
        final List<AttachedPhoto> attachedPhotos = List.of(
                new AttachedPhoto(UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8")),
                new AttachedPhoto(UUID.fromString("afb0cef2-851d-4a50-bb07-9cc15cbdc332")));

        final Review review = new Review(reviewId, userId, content, placeId, attachedPhotos);
        reviewRepository.save(review);

        // when
        final List<AttachedPhoto> newAttachedPhotoss = new ArrayList<>(review.getAttachedPhotos());
        newAttachedPhotoss.remove(0);
        review.updateAttachedPhotos(newAttachedPhotoss);

        reviewRepository.saveAndFlush(review);

        // then
        assertThat(review.getAttachedPhotos()).hasSize(1);
        assertThat(reviewRepository.findAll()).hasSize(1);
    }
}
