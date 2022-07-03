package com.example.travelermileageservice.domain.review.service;

import com.example.travelermileageservice.domain.review.entity.AttachedPhoto;
import com.example.travelermileageservice.domain.review.entity.Review;
import com.example.travelermileageservice.domain.review.repository.ReviewRepository;
import com.example.travelermileageservice.domain.review.service.dto.ReviewAddDto;
import com.example.travelermileageservice.domain.review.service.exception.ReviewException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewAddService {

    private final ReviewAddValidator reviewAddValidator;
    private final ReviewRepository reviewRepository;

    @Transactional
    public UUID add(final ReviewAddDto dto) {
        validate(dto);

        final List<AttachedPhoto> attachedPhotos = dto.getAttachedPhotoIds().stream()
                .map(AttachedPhoto::new)
                .collect(Collectors.toList());
        final Review review = new Review(dto.getReviewId(), dto.getUserId(), dto.getContent(), dto.getPlaceId(), attachedPhotos);

        return reviewRepository.save(review).getId();
    }

    /**
     * 사용자는 장소당 1개의 리뷰만 작성할 수 있습니다.
     */
    private void validate(final ReviewAddDto dto) {
        final Errors errors = new BeanPropertyBindingResult(dto, ReviewAddDto.class.getName());
        reviewAddValidator.validate(dto, errors);

        if (errors.hasErrors()) {
            throw new ReviewException(errors);
        }
    }
}
