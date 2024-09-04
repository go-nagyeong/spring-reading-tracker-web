package com.readingtracker.boochive.util;

import com.readingtracker.boochive.domain.Own;
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

    private Class<T> resourceType;
    private ResourceName resourceName;

    @PersistenceContext
    private EntityManager entityManager;

    public void init(Class<T> clazz) {
        this.resourceType = clazz;
        this.resourceName = ResourceName.fromClassName(clazz.getSimpleName());
    }

    public T checkAccessAndRetrieve(Long resourceId) {
        // 데이터 존재 여부 검사
        T resource = entityManager.find(resourceType, resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException(resourceName.getName());
        }

        // 데이터 접근 권한 검증
        checkAccess(resource);

        return resource;
    }

    public void checkAccess(T resource) {
        // 데이터 접근 권한 검증
        Long resourceOwnerId = resource.getUser().getId();
        Long currentUserId = CurrentUserContext.getUser().getId();
        if (!resourceOwnerId.equals(currentUserId)) {
            throw new ResourceAccessDeniedException(resourceName.getName());
        }
    }
}
