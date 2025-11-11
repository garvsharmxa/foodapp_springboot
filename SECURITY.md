# Security Policy

## Security Features

### Authentication & Authorization

1. **JWT-based Authentication**
   - Stateless authentication using JSON Web Tokens
   - Tokens are signed and validated on each request
   - Token expiration and refresh mechanisms should be implemented

2. **Role-Based Access Control (RBAC)**
   - Four user roles: ADMIN, CUSTOMER, RESTAURANT_OWNER, DELIVERY_PERSON
   - Endpoints are protected based on user roles
   - Principle of least privilege is followed

### CSRF Protection

This application is a stateless REST API using JWT authentication. CSRF protection is intentionally disabled because:

1. **Stateless Architecture**: The API doesn't use session cookies
2. **JWT in Headers**: Authentication tokens are sent in Authorization headers, not cookies
3. **Explicit Token Inclusion**: Each request must explicitly include the JWT token
4. **No Browser Auto-Send**: Unlike cookies, JWT tokens are not automatically sent by browsers

**Note**: If you plan to use cookie-based authentication in the future, CSRF protection should be re-enabled.

### Password Security

1. **BCrypt Password Encoding**
   - All passwords are hashed using BCrypt
   - Strong hashing algorithm resistant to rainbow table attacks
   - Salt is automatically generated and included in the hash

2. **Password Best Practices** (To be implemented)
   - Minimum password length requirements
   - Password complexity requirements
   - Password expiration policies
   - Prevention of password reuse

### Data Protection

1. **Database Security**
   - Database credentials should be stored securely (environment variables or secrets management)
   - Use strong, unique passwords for database access
   - Regular database backups
   - Encrypt sensitive data at rest

2. **Sensitive Data Handling**
   - Payment information is handled through Razorpay (PCI DSS compliant)
   - No credit card details are stored in the database
   - Transaction IDs and payment status are stored for tracking

### API Security

1. **Input Validation**
   - All user inputs should be validated
   - Use JPA/Hibernate validation annotations
   - Sanitize inputs to prevent injection attacks

2. **Rate Limiting** (To be implemented)
   - Implement rate limiting to prevent abuse
   - Protect against brute force attacks
   - Use Redis for distributed rate limiting

3. **HTTPS Only**
   - Always use HTTPS in production
   - Configure SSL/TLS certificates
   - Redirect HTTP to HTTPS

### Payment Security

1. **Razorpay Integration**
   - Payment signature verification on every transaction
   - Webhook signature validation
   - Secure API key management

2. **Payment Data**
   - Store only necessary payment metadata
   - Never store full credit card numbers
   - Log all payment transactions for audit

### Third-Party Dependencies

1. **Regular Updates**
   - Keep all dependencies up to date
   - Monitor security advisories
   - Use dependency vulnerability scanners

2. **Known Vulnerabilities**
   - Regularly scan with tools like OWASP Dependency-Check
   - Update vulnerable dependencies promptly

## Security Recommendations

### For Development

1. **Environment Variables**
   - Never commit sensitive data (API keys, passwords) to version control
   - Use `.env` files or environment variables
   - Add `.env` to `.gitignore`

2. **Razorpay Keys**
   ```properties
   # Use test keys for development
   razorpay.key.id=${RAZORPAY_KEY_ID}
   razorpay.key.secret=${RAZORPAY_KEY_SECRET}
   ```

3. **Database Credentials**
   ```properties
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   ```

### For Production

1. **HTTPS Configuration**
   - Obtain SSL/TLS certificates (Let's Encrypt, commercial CA)
   - Configure Spring Boot for HTTPS
   - Enable HTTP Strict Transport Security (HSTS)

2. **Secrets Management**
   - Use AWS Secrets Manager, Azure Key Vault, or HashiCorp Vault
   - Rotate credentials regularly
   - Implement proper access controls

3. **Logging & Monitoring**
   - Log all authentication attempts
   - Monitor failed login attempts
   - Set up alerts for suspicious activities
   - Never log sensitive data (passwords, tokens, credit cards)

4. **Database Security**
   - Use strong, unique passwords
   - Enable database encryption at rest
   - Use SSL/TLS for database connections
   - Implement regular backups

5. **API Security**
   - Implement rate limiting
   - Use API gateways for additional security layers
   - Enable CORS properly for frontend applications
   - Implement request size limits

6. **Error Handling**
   - Don't expose stack traces to clients
   - Use generic error messages for security-related errors
   - Log detailed errors server-side only

## Security Vulnerabilities

### CodeQL Analysis Results

**Finding**: CSRF protection disabled
**Status**: Accepted Risk
**Justification**: This is a stateless REST API using JWT authentication. CSRF protection is not applicable as:
- No session cookies are used
- JWT tokens are sent in Authorization headers
- Tokens must be explicitly included in each request
- Browser auto-submission does not occur

**Recommendation**: If cookie-based authentication is added in the future, enable CSRF protection immediately.

## Reporting Security Issues

If you discover a security vulnerability, please email security@foodapp.com with:
- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

**Do not** open public issues for security vulnerabilities.

## Security Checklist for Deployment

- [ ] Change all default credentials
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS with valid SSL certificate
- [ ] Configure firewall rules
- [ ] Implement rate limiting
- [ ] Set up monitoring and alerting
- [ ] Enable database encryption
- [ ] Regular security audits
- [ ] Backup and disaster recovery plan
- [ ] Security headers configuration (X-Frame-Options, X-XSS-Protection, etc.)
- [ ] CORS configuration for frontend domains
- [ ] Implement request size limits
- [ ] Enable audit logging
- [ ] Set up intrusion detection

## Compliance

### Payment Card Industry Data Security Standard (PCI DSS)

This application uses Razorpay for payment processing, which is PCI DSS Level 1 compliant. We do not store, process, or transmit credit card data directly, reducing PCI compliance scope significantly.

### General Data Protection Regulation (GDPR)

If operating in the EU or handling EU citizen data:
- Implement data retention policies
- Provide data export capabilities
- Implement right to be forgotten
- Obtain explicit consent for data processing
- Implement data breach notification procedures

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Razorpay Security](https://razorpay.com/docs/security/)
