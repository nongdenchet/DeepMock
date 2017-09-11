# DeepMock
- A library to make Unit Test more useful when using with mock
- Sometime we use mocking too much on all of our dependencies for UnitTest, this leaks to maintain tests are so painful. 
- In another hand, writing many tests this way bringing no benefit when doing refactoring

### Traditional way
- Doing this way if we decide to remove Mapper we have to modify our test
- Also mocking the mapper object is really useless
- If we try to extract a business object from `UserRepo` we have to create another dependency and keep maintain our test case in a painful way

```java
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
```

### With DeepMock
- By doing this way we can easily refactor code. 
- For example we can remove the Mapper object easily if needed
- Another example we can extract object from the `UserRepo` easily without updating our test case

```java
public class UserRepoTest {
    @Stub
    Schedulers schedulers = new TestSchedulers();
    @Mock
    UserStore userStore;
    @Target
    UserRepo userRepo;

    @Before
    public void setUp() throws Exception {
        DeepMock.inject(this);
        when(userStore.getUsers()).thenReturn(Arrays.asList(
                new UserEntity("name1", "name1@gmail.com"),
                new UserEntity("name2", "name2@gmail.com")
        ));
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
```
