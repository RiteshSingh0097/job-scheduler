package com.ritesh.scheduler.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuartzJob {

  @ToString.Include String key;

  String description;

  String cronExpression;
}
