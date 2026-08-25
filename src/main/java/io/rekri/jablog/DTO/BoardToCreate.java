package io.rekri.jablog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public class BoardToCreate {
    @NotNull private String boardName;
    @NotNull private String rule;
    @NotNull private String pass;
    @NotNull private String nickname;
    private int lifeCycleThreads;
    private int lifeCyclePosts;
    @Nullable private String transcription;
}
