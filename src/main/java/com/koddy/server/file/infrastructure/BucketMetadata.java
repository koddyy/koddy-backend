package com.koddy.server.file.infrastructure;

public interface BucketMetadata {
    /**
     * 사용자 프로필 업로드 버킷 -> profiles/{UUID}.{File Extension}
     */
    String MEMBER_PROFILE = "profiles/%s";
}
