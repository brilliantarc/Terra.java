package brilliantarc.terra;

import brilliantarc.terra.node.OperatingCompany;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;
import static brilliantarc.terra.DefaultSettings.*;

public class OperatingCompanyTests {

    @Test
    public void allOpcos() {
        List<OperatingCompany> opcos = terra.operatingCompanies().all();
        assertNotNull(opcos);
        assertTrue(opcos.size() > 0);
        assertTrue(Iterables.find(opcos, new Predicate<OperatingCompany>() {
            public boolean apply(OperatingCompany operatingCompany) {
                return TEST_PORTFOLIO.equals(operatingCompany.getSlug());
            }
        }) != null);
    }

    @Test
    public void getOpcoBySlug() {
        OperatingCompany opco = terra.operatingCompanies().operatingCompany(TEST_PORTFOLIO);
        assertNotNull(opco);
        assertEquals(TEST_PORTFOLIO, opco.getSlug());
        assertEquals("en", opco.getLanguage());
    }
}
