package ru.sapronov.api_testing

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.springframework.web.client.RestTemplate
import ru.sapronov.auth.dto.CreateAccountRequestDto
import ru.sapronov.auth.dto.CreateAndGetAccessTokenRequestDto
import ru.sapronov.client.HttpClient
import ru.sapronov.order.dto.CreateOrderRequestDto
import ru.sapronov.service.AuthService
import ru.sapronov.service.MailHogService
import ru.sapronov.service.OrderService
import ru.sapronov.service.TrackingService
import spock.lang.Specification

class BaseApiTest extends Specification {
    def DEFAULT_DURATION_TIME = 500

    MailHogService mailHogService
    AuthService authService
    TrackingService trackingService
    OrderService orderService

    def test1email = "test1@gmail.com"
    def test1password = "123"

    def test2email = "test2@gmail.com"
    def test2password = "234"

    def test3email = "test3@gmail.com"
    def test3password = "345"

    def setup() {
        mailHogService = new MailHogService()
        mailHogService.clear()
        clearAllDBs()
        def client = new HttpClient(new RestTemplate())
        authService = new AuthService(client)
        trackingService = new TrackingService(client)
        orderService = new OrderService(client)
    }

    def createAccTest1() {
        var dto = new CreateAccountRequestDto(test1email, test1password)
        authService.createAccount(dto)
    }

    def createAccTest2() {
        var dto = new CreateAccountRequestDto(test2email, test2password)
        authService.createAccount(dto)
    }

    def createAccTest3() {
        var dto = new CreateAccountRequestDto(test3email, test3password)
        authService.createAccount(dto)
    }

    def getConfirmationToken() {
        return mailHogService.getConfirmationTokenFromEmail()
    }

    def activateAccount(UUID token) {
        authService.activateAccount(token)
    }

    def getTest1AccessToken() {
        return authService.createAccessTokenAndGet(new CreateAndGetAccessTokenRequestDto(
                test1email,
                test1password
        )).body.accessToken()
    }

    def getTest2AccessToken() {
        return authService.createAccessTokenAndGet(new CreateAndGetAccessTokenRequestDto(
                test2email,
                test2password
        )).body.accessToken()
    }

    def getTest3AccessToken() {
        return authService.createAccessTokenAndGet(new CreateAndGetAccessTokenRequestDto(
                test3email,
                test3password
        )).body.accessToken()
    }

    String loginClient() {
        createAccTest1()
        waitBefore()
        def token = getConfirmationToken()
        mailHogService.clear()
        activateAccount(token)
        return getTest1AccessToken()
    }

    String loginCourier(boolean autoChangeStatus) {
        createAccTest2()
        waitBefore()
        def token = getConfirmationToken()
        mailHogService.clear()
        activateAccount(token)
        if (autoChangeStatus) { markUserAsCourier(test2email) }
        return getTest2AccessToken()
    }

    String loginAdmin() {
        createAccTest3()
        waitBefore()
        def token = getConfirmationToken()
        mailHogService.clear()
        activateAccount(token)
        markUserAsAdmin(test3email)
        return getTest3AccessToken()
    }

    def createOrder(CreateOrderRequestDto request, String clientAccessToken) {
        orderService.createOrder(request, clientAccessToken)
    }

    void waitBefore() {
        sleep(DEFAULT_DURATION_TIME)
    }

    Map commonParameters = [
            user: 'delivery-user',
            password: '123',
            driver: 'org.postgresql.Driver'
    ]
    Map authDbConnectionParameters = [url: 'jdbc:postgresql://localhost:6432/delivery-auth'] + commonParameters
    Map orderDbConnectionParameters = ['url': 'jdbc:postgresql://localhost:6433/delivery-order'] + commonParameters
    Map trackingDbConnectionParameters = ['url': 'jdbc:postgresql://localhost:6434/delivery-tracking'] + commonParameters


    def clearAllDBs() {
        clearAuthDB()
        clearOrderDB()
        clearTrackingDB()
    }

    def clearAuthDB() {
        clearDb(authDbConnectionParameters)
    }
    def clearOrderDB() {
        clearDb(orderDbConnectionParameters)
    }
    def clearTrackingDB() {
        clearDb(trackingDbConnectionParameters)
    }

    def clearDb(Map params) {
        Sql.withInstance(params) {
            Sql sql -> {
                sql.execute("SELECT 'TRUNCATE TABLE ' || quote_ident(schemaname) || '.' || quote_ident(tablename) || ' CASCADE;' \n" +
                        "FROM pg_tables \n" +
                        "WHERE schemaname NOT IN ('pg_catalog', 'information_schema');\n", { isResultSet, List<GroovyRowResult> result -> {
                    result.forEach {
                        sql.execute(it["?column?"] as String)
                    }
                }})
            }
        }
    }

    def markUserAsAdmin(String email) {
        markUserAs(email, "ADMIN")
    }

    def markUserAsCourier(String email) {
        markUserAs(email, "COURIER")
    }

    def markUserAs(String email, String role) {
        Sql.withInstance(authDbConnectionParameters) {
            sql -> {
                sql.execute("UPDATE public.persons SET role = ${role} WHERE email = ${email};")
            }
        }
    }


    def changeOrderStatusBySql(UUID orderId, String status) {
        Sql.withInstance(orderDbConnectionParameters) {
            sql -> {
                sql.execute("UPDATE public.orders SET status = ${status} WHERE id = ${orderId};")
            }
        }
    }

    UUID getOrderIdByClientEmail(String email) {
        def id
        Sql.withInstance(authDbConnectionParameters) {
            authSql -> {
                authSql.execute("SELECT id FROM public.persons WHERE email = ${email};", { _, authResult -> {
                    Sql.withInstance(orderDbConnectionParameters) {
                        orderSql -> {
                            orderSql.execute("SELECT id FROM public.orders WHERE person_id = ${authResult[0]["id"]};", {
                                __, orderResult -> {
                                    id = orderResult[0]["id"]
                                }
                            })
                        }
                    }
                }})
            }
        }
        return id
    }

    UUID getUserIdByEmail(String email) {
        def id
        Sql.withInstance(authDbConnectionParameters) {
            authSql -> {
                authSql.execute(
                        "SELECT id FROM public.persons WHERE email = ${email};",
                        { _, authResult -> {
                    id = authResult[0]["id"]
                }})
            }
        }
        return id
    }

    UUID getOrderCourierId(UUID orderId) {
        def id
        Sql.withInstance(orderDbConnectionParameters) {
            orderSql -> {
                orderSql.execute(
                        "SELECT delivery_person_id FROM public.orders WHERE id = ${orderId};",
                        { _, orderResult -> {
                            id = orderResult[0]["delivery_person_id"]
                        }})
            }
        }
        return id
    }

    String getOrderStatusFromDb(UUID orderId) {
        def status
        Sql.withInstance(orderDbConnectionParameters) {
            orderSql -> {
                orderSql.execute(
                        "SELECT status FROM public.orders WHERE id = ${orderId};",
                        { _, orderResult -> {
                            status = orderResult[0]["status"]
                        }})
            }
        }
        return status
    }
}
