package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Membership;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MembershipPopulatorTest {

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String PHONE_NUMBER = "phoneNumber";

    @InjectMocks
    private MembershipPopulator testObj;

    @Mock
    private Membership sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Membership());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceAndTargetAreNotNull_shouldPopulateMembership() {
        when(sourceMock.getMembershipId()).thenReturn(ID);
        when(sourceMock.getMembershipEmailAddress()).thenReturn(EMAIL);
        when(sourceMock.getMembershipName()).thenReturn(NAME);
        when(sourceMock.getMembershipPhoneNumber()).thenReturn(PHONE_NUMBER);

        final com.worldpay.internal.model.Membership target = new com.worldpay.internal.model.Membership();
        testObj.populate(sourceMock, target);

        assertThat(target.getMembershipId()).isEqualTo(ID);
        assertThat(target.getMembershipEmailAddress()).isEqualTo(EMAIL);
        assertThat(target.getMembershipName()).isEqualTo(NAME);
        assertThat(target.getMembershipPhoneNumber()).isEqualTo(PHONE_NUMBER);
    }
}
