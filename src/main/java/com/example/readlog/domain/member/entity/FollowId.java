package com.example.readlog.domain.member.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FollowId implements Serializable {
    private String follower;
    private String following;
}
