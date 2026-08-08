package io.rekri.jablog.http;

import io.rekri.jablog.controllers.Deleter;
import io.rekri.jablog.service.DeleterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DeleterTest {

    @Mock
    private DeleterService deleterService;

    @InjectMocks
    private Deleter deleter;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(deleter).build();
    }

    @Test
    public void thread_Success() throws Exception {
        String boardName = "placeholder";
        doNothing().when(deleterService).thread(anyLong());

        mockMvc.perform(delete("/deleter/"+boardName+"/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Redirect", "/" + boardName));

        verify(deleterService).thread(anyLong());
    }

    @Test
    public void post_Success() throws Exception {
        String boardName = "placeholder";
        String threadId = "0";
        doNothing().when(deleterService).post(anyLong());

        mockMvc.perform(delete("/deleter/"+boardName+"/"+threadId+"/0"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Redirect", "/"+boardName+"/"+threadId));

        verify(deleterService).post(anyLong());
    }
}