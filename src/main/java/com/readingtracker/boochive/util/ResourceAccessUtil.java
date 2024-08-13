package com.readingtracker.boochive.util;

import com.readingtracker.boochive.enums.ResourceName;
import com.readingtracker.boochive.exception.ResourceAccessDeniedException;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResourceAccessUtil<T extends Own> {

    @PersistenceContext
    private EntityManager entityManager;

    public T checkAccessAndRetrieve(Long resourceId, Class<T> resourceType) {
        ResourceName resourceName = ResourceName.fromClassName(resourceType.getSimpleName());

        // 데이터 존재 여부 검사
        T resource = entityManager.find(resourceType, resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException(resourceName.getName());
        }

        // 데이터 접근 권한 검증
        Long resourceOwnerId = resource.getUser().getId();
        Long currentUserId = CurrentUserContext.getUser().getId();
        if (!resourceOwnerId.equals(currentUserId)) {
            throw new ResourceAccessDeniedException(resourceName.getName());
        }

        return resource;
    }
}
