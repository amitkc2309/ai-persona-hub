package com.ai.persona.profiles_conversation.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedProfiles {
    private int totalCount;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private List<ProfileDto> profiles;
}
