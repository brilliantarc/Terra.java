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
                return "PKT".equals(operatingCompany.getSlug());
            }
        }) != null);
    }

    @Test
    public void getOpcoBySlug() {
        OperatingCompany opco = terra.operatingCompanies().operatingCompany("PKT");
        assertNotNull(opco);
        assertEquals("PKT", opco.getSlug());
        assertEquals("pl", opco.getLanguage());
    }
}
