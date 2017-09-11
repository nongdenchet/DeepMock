package com.rain.deepmock;

import com.rain.deepmock.data.User;
import com.rain.deepmock.data.UserEntity;
import com.rain.deepmock.data.UserMapper;
import com.rain.deepmock.data.UserRepo;
import com.rain.deepmock.data.UserStore;
import com.rain.deepmock.schedulers.TestSchedulers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserRepoMockTest {
    private UserRepo userRepo;

    @Mock
    UserStore userStore;
    @Mock
    UserMapper userMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepo = new UserRepo(new TestSchedulers(), userMapper, userStore);
        when(userStore.getUsers()).thenReturn(Arrays.asList(
                new UserEntity("name1", "name1@gmail.com"),
                new UserEntity("name2", "name2@gmail.com")
        ));
        when(userMapper.toModel(any(UserEntity.class))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                UserEntity userEntity = invocation.getArgument(0);
                return new User(userEntity.getUsername(), userEntity.getEmail());
            }
        });
    }

    @Test
    public void getUsers_returnCorrectSize() throws Exception {
        List<User> users = userRepo.getUsers();
        assertEquals(2, users.size());
    }

    @Test
    public void getUsers_returnCorrectNames() throws Exception {
        List<User> users = userRepo.getUsers();
        assertEquals("name1", users.get(0).getUsername());
        assertEquals("name2", users.get(1).getUsername());
    }

    @Test
    public void getUsers_returnCorrectEmails() throws Exception {
        List<User> users = userRepo.getUsers();
        assertEquals("name1@gmail.com", users.get(0).getEmail());
        assertEquals("name2@gmail.com", users.get(1).getEmail());
    }
}
