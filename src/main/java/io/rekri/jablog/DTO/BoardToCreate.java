package io.rekri.jablog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@Data
@AllArgsConstructor
public class BoardToCreate {
    @NonNull private String boardName;
    @NonNull private String rule;
    @NonNull private String pass;
    @NonNull private String nickname;
    private int lifeCycleThreads;
    private int lifeCyclePosts;
    @Nullable private String transcription;
}
