package com.example.BigProject_25.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lostID;

    @Column(name = "img_filename", nullable = false)
    private String imgFilename;

    @Column(name = "lost_name", nullable = false)
    private String lostName;

    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Column(name = "category", nullable = false)
    private String category;
}
